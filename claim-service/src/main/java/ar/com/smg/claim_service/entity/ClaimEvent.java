package ar.com.smg.claim_service.entity;

import ar.com.smg.claim_service.utils.EventType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimEvent {
  private String eventType;
  private Long claimId;
  private String claimStatus;
  private LocalDateTime timestamp;
}
