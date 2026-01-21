package ar.com.smg.ia_starter.client;

import ar.com.smg.ia_starter.model.ClassificationResult;
import ar.com.smg.ia_starter.model.DocumentType;
import ar.com.smg.ia_starter.properties.AIProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

  private final AIProperties aiProperties;
  private final ObjectMapper objectMapper;
  private final WebClient webClient;

  private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

  /**
   * Clasifica documento usando texto extraído
   */
  public ClassificationResult classifyFromText(String documentKey, String extractedText) {
    long startTime = System.currentTimeMillis();

    try {
      String prompt = buildClassificationPrompt(extractedText);

      Map<String, Object> requestBody = buildTextRequest(prompt);

      String response = callGeminiApi(requestBody);

      return parseGeminiResponse(documentKey, response, startTime);

    } catch (Exception e) {
      log.error("Error clasificando con Gemini (texto): {}", e.getMessage(), e);
      return buildErrorResult(documentKey, e.getMessage(), startTime);
    }
  }

  /**
   * Clasifica documento enviando imagen/PDF directamente (multimodal)
   */
  public ClassificationResult classifyFromImage(String documentKey, byte[] fileBytes, String mimeType) {
    long startTime = System.currentTimeMillis();

    try {
      String prompt = buildClassificationPrompt("");

      Map<String, Object> requestBody = buildMultimodalRequest(prompt, fileBytes, mimeType);

      String response = callGeminiApi(requestBody);

      return parseGeminiResponse(documentKey, response, startTime);

    } catch (Exception e) {
      log.error("Error clasificando con Gemini (imagen): {}", e.getMessage(), e);
      return buildErrorResult(documentKey, e.getMessage(), startTime);
    }
  }

  /**
   * Construye request solo con texto
   */
  private Map<String, Object> buildTextRequest(String prompt) {
    Map<String, Object> request = new HashMap<>();

    // Contents
    Map<String, Object> part = Map.of("text", prompt);
    Map<String, Object> content = Map.of("parts", List.of(part));
    request.put("contents", List.of(content));

    // Generation config
    Map<String, Object> generationConfig = Map.of(
            "temperature", aiProperties.getTemperature(),
            "maxOutputTokens", aiProperties.getMaxTokens(),
            "candidateCount", 1
    );
    request.put("generationConfig", generationConfig);

    return request;
  }

  /**
   * Construye request multimodal (texto + imagen/PDF)
   */
  private Map<String, Object> buildMultimodalRequest(String prompt, byte[] fileBytes, String mimeType) {
    Map<String, Object> request = new HashMap<>();

    // Encode file to base64
    String base64Data = Base64.getEncoder().encodeToString(fileBytes);

    // Parts: texto + imagen
    List<Map<String, Object>> parts = List.of(
            Map.of("text", prompt),
            Map.of("inline_data", Map.of(
                    "mime_type", mimeType,
                    "data", base64Data
            ))
    );

    Map<String, Object> content = Map.of("parts", parts);
    request.put("contents", List.of(content));

    // Generation config
    Map<String, Object> generationConfig = Map.of(
            "temperature", aiProperties.getTemperature(),
            "maxOutputTokens", aiProperties.getMaxTokens()
    );
    request.put("generationConfig", generationConfig);

    return request;
  }

  /**
   * Llama a la API de Gemini
   */
  private String callGeminiApi(Map<String, Object> requestBody) {
    String endpoint = String.format("%s%s:generateContent?key=%s",
            GEMINI_API_URL,
            aiProperties.getModel(),
            aiProperties.getApiKey()
    );

    log.debug("Llamando a Gemini API: {}", aiProperties.getModel());

    return webClient.post()
            .uri(endpoint)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
            .doOnError(error -> log.error("Error en llamada a Gemini: {}", error.getMessage()))
            .block();
  }

  /**
   * Parsea la respuesta de Gemini
   */
  private ClassificationResult parseGeminiResponse(String documentKey, String jsonResponse, long startTime) {
    try {
      JsonNode root = objectMapper.readTree(jsonResponse);

      // Extraer texto de la respuesta
      String aiText = root.path("candidates")
              .get(0)
              .path("content")
              .path("parts")
              .get(0)
              .path("text")
              .asText();

      log.debug("Respuesta de Gemini: {}", aiText);

      // Limpiar JSON (Gemini a veces agrega markdown)
      String cleanJson = aiText
              .replaceAll("```json", "")
              .replaceAll("```", "")
              .trim();

      // Parsear resultado
      JsonNode resultJson = objectMapper.readTree(cleanJson);

      String typeStr = resultJson.path("type").asText();
      Double confidence = resultJson.path("confidence").asDouble();
      String reasoning = resultJson.path("reasoning").asText();

      DocumentType documentType = DocumentType.valueOf(typeStr);

      return ClassificationResult.builder()
              .documentKey(documentKey)
              .primaryType(documentType)
              .confidence(confidence)
              .reasoning(reasoning)
              .classifiedAt(LocalDateTime.now())
              .modelUsed(aiProperties.getModel())
              .processingTimeMs(System.currentTimeMillis() - startTime)
              .build();

    } catch (Exception e) {
      log.error("Error parseando respuesta de Gemini: {}", jsonResponse, e);
      return buildErrorResult(documentKey, "Error parseando: " + e.getMessage(), startTime);
    }
  }

  /**
   * Construye el prompt de clasificación
   */
  private String buildClassificationPrompt(String documentText) {
    String availableTypes = String.join("\n",
            List.of(
                    "- RECETA_MEDICA: Receta Médica",
                    "- RESULTADO_LABORATORIO: Resultado de Laboratorio",
                    "- INFORME_MEDICO: Informe Médico",
                    "- RADIOGRAFIA: Radiografía",
                    "- FACTURA_MEDICA: Factura Médica",
                    "- HISTORIA_CLINICA: Historia Clínica",
                    "- ORDEN_MEDICA: Orden Médica",
                    "- CONSENTIMIENTO_INFORMADO: Consentimiento Informado",
                    "- OTRO: Otro Documento"
            )
    );

    if (documentText != null && !documentText.isEmpty()) {
      return String.format("""
                Sos un experto en clasificación de documentos médicos del sistema de salud argentino.
                
                Clasificá este documento en UNA de estas categorías:
                %s
                
                DOCUMENTO:
                %s
                
                Respondé SOLO en JSON:
                {
                  "type": "CATEGORIA",
                  "confidence": 0.95,
                  "reasoning": "explicación breve"
                }
                """, availableTypes, documentText);
    } else {
      // Modo multimodal (ya tiene la imagen)
      return String.format("""
                Sos un experto en clasificación de documentos médicos del sistema de salud argentino.
                
                Analizá la imagen del documento y clasificalo en UNA de estas categorías:
                %s
                
                Considerá:
                - Membrete, logos, sellos
                - Estructura del documento
                - Terminología médica argentina
                - Firmas y matrículas profesionales
                
                Respondé SOLO en JSON:
                {
                  "type": "CATEGORIA",
                  "confidence": 0.95,
                  "reasoning": "explicación breve"
                }
                """, availableTypes);
    }
  }

  private ClassificationResult buildErrorResult(String documentKey, String errorMessage, long startTime) {
    return ClassificationResult.builder()
            .documentKey(documentKey)
            .primaryType(DocumentType.NO_CLASIFICABLE)
            .confidence(0.0)
            .errorMessage(errorMessage)
            .classifiedAt(LocalDateTime.now())
            .processingTimeMs(System.currentTimeMillis() - startTime)
            .build();
  }
}
