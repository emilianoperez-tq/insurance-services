package ar.com.smg.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  public void sendEmail(String to, String subject, String body) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom("noreply@tuapp.com");
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);

      mailSender.send(message);
      log.info("✅ Email enviado exitosamente a: {}", to);

    } catch (Exception e) {
      log.error("❌ Error enviando email a {}: {}", to, e.getMessage());
      throw new RuntimeException("Error enviando email", e);
    }
  }
}
