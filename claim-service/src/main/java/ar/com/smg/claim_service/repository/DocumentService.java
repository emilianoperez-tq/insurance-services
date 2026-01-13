package ar.com.smg.claim_service.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService {
  String uploadDocument(MultipartFile file) throws IOException;
  void deleteDocument(String documentId) throws IOException;
}
