package ar.com.smg.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.net.URI;

/**
 * Configuración compartida de AWS Secrets Manager
 * Esta clase puede ser usada por todos los microservicios
 */
@Slf4j
@Configuration
public class SecretsManagerConfig {

  @Value("${aws.secretsmanager.endpoint:http://localstack:4566}")
  private String endpoint;

  @Value("${aws.secretsmanager.region:us-east-1}")
  private String region;

  @Value("${aws.secretsmanager.enabled:true}")
  private boolean enabled;

  @Value("${aws.access-key-id:test}")
  private String accessKeyId;

  @Value("${aws.secret-access-key:test}")
  private String secretAccessKey;

  @Bean
  public SecretsManagerClient secretsManagerClient() {
    if (!enabled) {
      log.warn("Secrets Manager is disabled");
      return null;
    }

    log.info("Configuring Secrets Manager Client");
    log.info("Endpoint: {}", endpoint);
    log.info("Region: {}", region);

    var builder = SecretsManagerClient.builder()
            .region(Region.of(region));

    // Configuración para LocalStack o desarrollo local
    if (endpoint != null && !endpoint.isEmpty()) {
      log.info("Using custom endpoint: {}", endpoint);
      builder.endpointOverride(URI.create(endpoint))
              .credentialsProvider(StaticCredentialsProvider.create(
                      AwsBasicCredentials.create(accessKeyId, secretAccessKey)
              ));
    }

    return builder.build();
  }
}
