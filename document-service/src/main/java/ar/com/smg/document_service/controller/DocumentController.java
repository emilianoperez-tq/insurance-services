package ar.com.smg.document_service.controller;

import ar.com.smg.document_service.entity.Document;
import ar.com.smg.document_service.model.DocumentCategory;
import ar.com.smg.document_service.service.DocumentService;
import ar.com.smg.document_service.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {

  private final DocumentService documentService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Document uploadDocument(
          @RequestPart(value = "file", required = true) MultipartFile file,
          @RequestParam(value = "categoria", defaultValue  = "GENERAL") String categoria
  ) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("File must not be null or empty");
    }

    DocumentCategory documentCategory = DocumentCategory.fromString(categoria);

    return documentService.saveDocument(file);
  }

  @GetMapping("/{key}/download")
  public byte[] downloadDocument(@PathVariable Long key) throws IOException {
    byte[] file = new byte[1024];

    return file;
  }
}
