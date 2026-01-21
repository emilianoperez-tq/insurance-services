package ar.com.smg.ia_starter.service;

import ar.com.smg.ia_starter.client.GeminiClient;
import ar.com.smg.ia_starter.model.ClassificationResult;
import ar.com.smg.ia_starter.model.DocumentType;
import ar.com.smg.ia_starter.properties.AIProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentClassifierService {

  private final GeminiClient geminiClient;
  private final AIProperties aiProperties;

  /**
   * Clasifica documento desde texto extraído
   */
  @Retryable(
          maxAttempts = 3,
          backoff = @Backoff(delay = 2000, multiplier = 2),
          retryFor = {Exception.class}
  )
  public ClassificationResult classifyDocument(String documentKey, String extractedText) {
    log.info("Clasificando documento (texto): {}", documentKey);

    if (extractedText == null || extractedText.trim().isEmpty()) {
      return geminiClient.classifyFromText(documentKey, "");
    }

    // Truncar si es muy largo (Gemini soporta hasta ~30K tokens)
    String textToAnalyze = truncateText(extractedText, 10000);

    return geminiClient.classifyFromText(documentKey, textToAnalyze);
  }

  /**
   * Clasifica documento desde bytes (imagen/PDF) - MODO MULTIMODAL
   */
  @Retryable(
          maxAttempts = 3,
          backoff = @Backoff(delay = 2000, multiplier = 2),
          retryFor = {Exception.class}
  )
  public ClassificationResult classifyDocumentMultimodal(
          String documentKey,
          byte[] fileBytes,
          String mimeType) {

    log.info("Clasificando documento (multimodal): {} - type: {}", documentKey, mimeType);

    if (!aiProperties.getUseVision()) {
      throw new UnsupportedOperationException("Visión no habilitada en configuración");
    }

    return geminiClient.classifyFromImage(documentKey, fileBytes, mimeType);
  }

  private String truncateText(String text, int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }
    return text.substring(0, maxLength) + "\n\n[Texto truncado...]";
  }
}
