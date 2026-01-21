package ar.com.smg.document_service.service.document;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentTextExtractor {
  boolean supports(String contentType);
  String extract(MultipartFile file);
}
