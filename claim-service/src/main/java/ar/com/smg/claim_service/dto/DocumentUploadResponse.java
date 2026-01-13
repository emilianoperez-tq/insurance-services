package ar.com.smg.claim_service.dto;

import lombok.Data;

@Data
public class DocumentUploadResponse {
  private String documentUrl;
  private String documentId;
}
