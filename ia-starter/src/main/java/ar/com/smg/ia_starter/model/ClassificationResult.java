package ar.com.smg.ia_starter.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ClassificationResult {
  private String documentKey;
  private DocumentType primaryType;
  private DocumentType secondaryType; // Por si hay ambigüedad
  private Double confidence; // 0.0 - 1.0
  private String reasoning; // Explicación del modelo
  private Map<DocumentType, Double> allScores; // Scores de todas las categorías
  private LocalDateTime classifiedAt;
  private String modelUsed; // "gpt-4", "llama3", etc.
  private Long processingTimeMs;
  private String errorMessage; // Si falló

  public boolean isSuccessful() {
    return errorMessage == null && primaryType != DocumentType.NO_CLASIFICABLE;
  }

  public boolean isHighConfidence() {
    return confidence != null && confidence >= 0.8;
  }
}
