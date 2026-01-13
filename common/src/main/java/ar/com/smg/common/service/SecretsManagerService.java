package ar.com.smg.common.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio compartido para obtener secretos de AWS Secrets Manager
 * Puede ser usado por todos los microservicios
 */
@Slf4j
@Service
public class SecretsManagerService {

  private final SecretsManagerClient secretsManagerClient;
  private final ObjectMapper objectMapper;

  public SecretsManagerService(SecretsManagerClient secretsManagerClient) {
    this.secretsManagerClient = secretsManagerClient;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Obtiene un secreto como String
   */
  @Cacheable(value = "secrets", key = "#secretName")
  public String getSecretString(String secretName) {
    log.info("Retrieving secret: {}", secretName);

    try {
      GetSecretValueRequest request = GetSecretValueRequest.builder()
              .secretId(secretName)
              .build();

      GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);

      log.debug("Secret retrieved successfully: {}", secretName);
      return response.secretString();

    } catch (ResourceNotFoundException e) {
      log.error("Secret not found: {}", secretName, e);
      throw new RuntimeException("Secret not found: " + secretName, e);
    } catch (Exception e) {
      log.error("Error retrieving secret: {}", secretName, e);
      throw new RuntimeException("Error retrieving secret: " + secretName, e);
    }
  }

  /**
   * Obtiene un secreto como Map
   */
  @Cacheable(value = "secretMaps", key = "#secretName")
  public Map<String, String> getSecretAsMap(String secretName) {
    String secretString = getSecretString(secretName);

    try {
      JsonNode jsonNode = objectMapper.readTree(secretString);
      Map<String, String> secretMap = new HashMap<>();

      jsonNode.fields().forEachRemaining(entry ->
              secretMap.put(entry.getKey(), entry.getValue().asText())
      );

      return secretMap;

    } catch (Exception e) {
      log.error("Error parsing secret as JSON: {}", secretName, e);
      throw new RuntimeException("Error parsing secret: " + secretName, e);
    }
  }

  /**
   * Obtiene un valor específico de un secreto
   */
  public String getSecretValue(String secretName, String key) {
    Map<String, String> secretMap = getSecretAsMap(secretName);
    String value = secretMap.get(key);

    if (value == null) {
      log.warn("Key '{}' not found in secret '{}'", key, secretName);
    }

    return value;
  }

  /**
   * Obtiene un secreto como objeto personalizado
   */
  public <T> T getSecretAsObject(String secretName, Class<T> clazz) {
    String secretString = getSecretString(secretName);

    try {
      return objectMapper.readValue(secretString, clazz);
    } catch (Exception e) {
      log.error("Error parsing secret as object: {}", secretName, e);
      throw new RuntimeException("Error parsing secret: " + secretName, e);
    }
  }
}
