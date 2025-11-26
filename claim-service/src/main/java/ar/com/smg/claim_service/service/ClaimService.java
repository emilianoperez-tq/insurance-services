package ar.com.smg.claim_service.service;

import ar.com.smg.claim_service.entity.Claim;
import ar.com.smg.claim_service.entity.ClaimEvent;
import ar.com.smg.claim_service.repository.ClaimRepository;
import ar.com.smg.claim_service.utils.EventType;
import ar.com.smg.claim_service.utils.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {

  private final ClaimRepository claimRepository;
  private final RabbitTemplate rabbitTemplate;

  public Claim createClaim(Claim claim) {
    Claim savedClaim = claimRepository.save(claim);

    String eventType = EventType.CLAIM_CREATED;

    publishClaimEvent(claim, eventType);

    log.info("Published CLAIM_CREATED event for claim ID: {}", savedClaim.getId());

    return savedClaim;
  }

  public List<Claim> getAllClaims() {
    return claimRepository
            .findAll()
            .stream()
            .filter(clam -> clam.getStatus().equals("PENDING"))
            .toList();
  }

  public Claim getClaimById(Long id) {
    return claimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Claim not found"));
  }

  public Claim updateClaimStatus(Long id, String status) {
    Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Claim not found"));

    claim.setStatus(status);

    String eventType = EventType.getEventType(status);

    publishClaimEvent(claim, eventType);

    return claimRepository.save(claim);
  }

  private void publishClaimEvent(Claim claim, String eventType) {
    ClaimEvent event = ClaimEvent.builder()
            .eventType(eventType)
            .claimId(claim.getId())
            .claimStatus(claim.getStatus())
            .build();

    try {
      rabbitTemplate.convertAndSend(
              RabbitMQConstants.CLAIMS_EXCHANGE,
              RabbitMQConstants.CLAIMS_ROUTING_KEY,
              event
      );
      log.info("✅ Evento publicado: {} - Claim ID: {}", eventType, claim.getId());
    } catch (Exception e) {
      log.error("❌ Error publicando evento: {}", e.getMessage(), e);
    }
  }
}
