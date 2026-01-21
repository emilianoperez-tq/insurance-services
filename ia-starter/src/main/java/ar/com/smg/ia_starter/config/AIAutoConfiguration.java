package ar.com.smg.ia_starter.config;

import ar.com.smg.ia_starter.client.GeminiClient;
import ar.com.smg.ia_starter.properties.AIProperties;
import ar.com.smg.ia_starter.service.DocumentClassifierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@EnableRetry
@EnableConfigurationProperties(AIProperties.class)
public class AIAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() {
    return WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
            .build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "insurance.ai", name = "provider", havingValue = "gemini")
  @ConditionalOnMissingBean
  public GeminiClient geminiClient(AIProperties properties, ObjectMapper objectMapper, WebClient webClient) {
    log.info("Configurando Gemini Client - modelo: {}", properties.getModel());

    if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
      throw new IllegalStateException("insurance.ai.api-key es obligatorio para Gemini");
    }

    return new GeminiClient(properties, objectMapper, webClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public DocumentClassifierService documentClassifierService(
          GeminiClient geminiClient,
          AIProperties aiProperties) {
    log.info("Iniciando DocumentClassifierService con Gemini");
    return new DocumentClassifierService(geminiClient, aiProperties);
  }
}
