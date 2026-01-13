package ar.com.smg.common.config;

import ar.com.smg.common.service.SecretsManagerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBeansConfig {

  @Bean
  public SecretsManagerService secretsManagerService(SecretsManagerService secretsManagerService) {
    return secretsManagerService;
  }
}
