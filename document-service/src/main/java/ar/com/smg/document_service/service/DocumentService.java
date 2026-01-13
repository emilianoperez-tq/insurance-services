package ar.com.smg.document_service.service;

import ar.com.smg.document_service.entity.Document;
import ar.com.smg.document_service.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

  private final DocumentRepository documentRepository;
  private final S3Service s3Service;

  public Document saveDocument(MultipartFile file) throws IOException {
    String fileUrl = s3Service.uploadDocument(file);

    Document document = Document.builder()
            .fileName(file.getOriginalFilename())
            .fileType(file.getContentType())
            .filePath(fileUrl)
            .uploadDate(new Date())
            .build();

    Document savedDocument = documentRepository.save(document);
    log.info("Document saved with ID: {}", savedDocument.getId());
    return savedDocument;
  }

  public byte[] downloadDocument(Long documentId) throws IOException {
    Document document = getDocumentById(documentId);
    return s3Service.downloadDocument(document.getFilePath());
  }

  private Document getDocumentById(Long id) {
    return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
  }

  public Document getDocuments() {
    return documentRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No documents found"));
  }
}
