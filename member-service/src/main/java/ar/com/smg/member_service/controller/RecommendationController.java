package ar.com.smg.member_service.controller;

import ar.com.smg.member_service.model.dto.RecommendationRequest;
import ar.com.smg.member_service.model.dto.RecommendationResponse;
import ar.com.smg.member_service.service.recommendation.PlanRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

  private final PlanRecommendationService recommendationService;

  /**
   * GET /api/members/{memberId}/recommendations
   *
   * Genera recomendaciones AUTOMÁTICAMENTE desde datos de BD.
   * NO requiere request body.
   *
   * El sistema:
   * 1. Lee el perfil del miembro desde la BD
   * 2. Obtiene planes elegibles desde la BD
   * 3. Usa IA para analizar y recomendar
   * 4. Devuelve recomendaciones personalizadas
   */
  @GetMapping("/{memberId}/recommendations")
  public ResponseEntity<RecommendationResponse> getRecommendations(
          @PathVariable Long memberId) {

    log.info("📋 Solicitud de recomendaciones automáticas para member: {}", memberId);

    RecommendationResponse response = recommendationService
            .getRecommendationsForMember(memberId);

    return ResponseEntity.ok()
            .header("X-From-Cache", String.valueOf(response.getFromCache()))
            .header("X-Model-Used", response.getModelUsed())
            .header("X-Plans-Analyzed", String.valueOf(response.getRecommendations().size()))
            .body(response);
  }

  /**
   * POST /api/members/{memberId}/recommendations/refresh
   *
   * Fuerza la regeneración de recomendaciones ignorando caché.
   * Útil cuando el perfil del miembro ha cambiado.
   */
  @PostMapping("/{memberId}/recommendations/refresh")
  public ResponseEntity<RecommendationResponse> refreshRecommendations(
          @PathVariable Long memberId) {

    log.info("🔄 Refrescando recomendaciones para member: {}", memberId);

    // TODO: Implementar lógica para invalidar caché específico
    // Por ahora, simplemente llama al servicio
    RecommendationResponse response = recommendationService
            .getRecommendationsForMember(memberId);

    return ResponseEntity.ok(response);
  }
}
