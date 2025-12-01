package ar.com.smg.auth_service.controller;

import ar.com.smg.auth_service.dto.LoginRequest;
import ar.com.smg.auth_service.dto.TokenResponse;
import ar.com.smg.auth_service.service.KeycloakAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final KeycloakAuthService keycloakAuthService;

  @GetMapping("/health")
  public String healthCheck() {
    return "Auth Service is running and accessible.";
  }

  @GetMapping("/login")
  public String loginGet() {
    return "Please use POST method to login.";
  }

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public TokenResponse login(@RequestBody LoginRequest request) {
    try {
      return keycloakAuthService.authenticate(request.getUsername(), request.getPassword());
    } catch (Exception e) {
      throw new RuntimeException("Authentication failed: " + e.getMessage());
    }
  }

  @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
  public TokenResponse refresh(@RequestBody String refreshToken) {
    try {
      return keycloakAuthService.refreshToken(refreshToken);
    } catch (Exception e) {
      throw new RuntimeException("Token refresh failed: " + e.getMessage());
    }
  }
}
