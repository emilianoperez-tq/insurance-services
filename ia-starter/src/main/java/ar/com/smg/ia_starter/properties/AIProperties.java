package ar.com.smg.ia_starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "insurance.ai")
public class AIProperties {

  /**
   * Provider de IA: "openai", "ollama", "gemini"
   */
  private String provider = "gemin";

  /**
   * API Key (solo para proveedores cloud)
   */
  private String apiKey;

  /**
   * URL base del servicio (para Ollama local)
   */
  private String baseUrl = "http://localhost:11434";

  /**
   * Modelo a utilizar
   * Opciones para Gemini:
   * - gemini-pro (texto)
   * - gemini-pro-vision (multimodal - RECOMENDADO)
   * - gemini-1.5-flash (rápido y barato)
   * - gemini-1.5-pro (más potente)
   */
  private String model = "gemini-1.5-flash";

  /**
   * Timeout en segundos
   */
  private Integer timeoutSeconds = 30;

  /**
   * Temperatura del modelo (0.0 = determinista, 1.0 = creativo)
   */
  private Double temperature = 0.1;

  /**
   * Máximo de tokens en la respuesta
   */
  private Integer maxTokens = 500;

  /**
   * Habilitar clasificación automática
   */
  private Boolean autoClassify = true;

  /**
   * Reintentos en caso de error
   */
  private Integer retries = 2;

  /**
   * Usar visión (enviar imágenes/PDFs directamente)
   */
  private Boolean useVision = true;

  /**
   * Region de Gemini (opcional)
   */
  private String region = "us-central1";
}
