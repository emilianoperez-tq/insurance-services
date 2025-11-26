package ar.com.smg.notification_service.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimEvent {
  private String eventType;
  private Long claimId;
  private String claimStatus;
  private LocalDateTime timestamp;
}
