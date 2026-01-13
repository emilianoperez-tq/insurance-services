package ar.com.smg.auth_service.service;

import ar.com.smg.auth_service.dto.TokenResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakAuthService {

  @Value("${keycloak.server-url}")
  private String serverUrl;

  @Value("${keycloak.client-id}")
  private String clientId;

  @Value("${keycloak.client-secret}")
  private String clientSecret;

  private WebClient webClient;

  @PostConstruct
  public void logProperties() {
    log.info("Keycloak configuration:");
    log.info("server-url = {}", serverUrl);
    log.info("client-id = {}", clientId);
    log.info("client-secret = {}", clientSecret != null ? "***" : "null");
  }

  public KeycloakAuthService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public TokenResponse authenticate(String username, String password) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "password");
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    formData.add("username", username);
    formData.add("password", password);

    WebClient.ResponseSpec response = webClient.post()
            .uri(serverUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve();

    return response.bodyToMono(TokenResponse.class).block();
  }

  public TokenResponse refreshToken(String refreshToken) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "refresh_token");
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    formData.add("refresh_token", refreshToken);

    return webClient.post()
            .uri(serverUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();
  }
}
