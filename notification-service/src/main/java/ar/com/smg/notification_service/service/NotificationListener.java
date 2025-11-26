package ar.com.smg.notification_service.service;

import ar.com.smg.notification_service.config.RabbitMQConfig;
import ar.com.smg.notification_service.models.ClaimEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ar.com.smg.notification_service.utils.EventType;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

  private final EmailService emailService;
  private final String SUPPORT_EMAIL = "emi.perez997@gmail.com";

  @RabbitListener(queues = RabbitMQConfig.CLAIMS_QUEUE)
  public void handleClaimEvent(ClaimEvent event) {
    log.info("Received claim event: {}", event);

    try {
      switch (event.getEventType()) {
        case "CLAIM_CREATED":
          sendClaimCreatedNotification(event);
          break;
        case "CLAIM_APPROVED":
          sendClaimApprovedNotification(event);
          break;
        case "CLAIM_REJECTED":
          sendClaimRejectedNotification(event);
          break;
      }
    } catch (Exception e) {
      log.error("Error processing claim event", e);
    }
  }

  private void sendClaimCreatedNotification(ClaimEvent event) {
    String subject = "Reclamo Recibido - ID: " + event.getClaimId();
    String body = "Su reclamo ha sido registrado exitosamente...";

    emailService.sendEmail(SUPPORT_EMAIL, subject, body);
  }

  private void sendClaimApprovedNotification(ClaimEvent event) {
    String subject = "Reclamo Aprobado - ID: " + event.getClaimId();
    String body = "Su reclamo ha sido aprobado...";

    emailService.sendEmail(SUPPORT_EMAIL, subject, body);
  }

  private void sendClaimRejectedNotification(ClaimEvent event) {
    String subject = "Reclamo Rechazado - ID: " + event.getClaimId();
    String body = "Lamentamos informarle que su reclamo ha sido rechazado...";

    emailService.sendEmail(SUPPORT_EMAIL, subject, body);
  }
}
