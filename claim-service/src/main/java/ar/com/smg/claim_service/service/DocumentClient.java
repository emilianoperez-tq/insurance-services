package ar.com.smg.claim_service.service;

import ar.com.smg.claim_service.dto.DocumentUploadResponse;
import ar.com.smg.claim_service.repository.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DocumentClient implements DocumentService {
  private final WebClient.Builder webClientBuilder;

  @Value("${document.service.url}")
  private String documentServiceUrl;

  @Override
  public String uploadDocument(MultipartFile file) {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("file", file.getResource());

    DocumentUploadResponse response = webClientBuilder.build()
            .post()
            .uri(documentServiceUrl + "/upload")
            .bodyValue(builder.build())
            .retrieve()
            .bodyToMono(DocumentUploadResponse.class)
            .block();

    if (response != null) {
      return response.getDocumentUrl();
    }

    throw new RuntimeException("Failed to upload document");
  }

  @Override
  public void deleteDocument(String documentId) {
    // Mock implementation: In a real scenario, this would delete the file from the storage service.
  }
}
