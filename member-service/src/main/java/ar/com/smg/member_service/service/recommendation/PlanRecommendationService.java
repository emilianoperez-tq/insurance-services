package ar.com.smg.member_service.service.recommendation;

import ar.com.smg.member_service.exception.AIRecommendationException;
import ar.com.smg.member_service.model.dto.PlanRecommendation;
import ar.com.smg.member_service.model.dto.RecommendationResponse;
import ar.com.smg.member_service.model.entity.InsurancePlan;
import ar.com.smg.member_service.model.entity.Member;
import ar.com.smg.member_service.service.insurance.InsurancePlanService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanRecommendationService {

  private final ChatModel chatModel;
  private final PromptService promptService;
  private final ObjectMapper objectMapper;
  private final MemberService memberService;
  private final InsurancePlanService planService;

  /**
   * Genera recomendaciones AUTOMÁTICAMENTE desde datos de BD
   * NO requiere request body, solo el ID del miembro
   *
   * @param memberId ID del miembro
   * @return Recomendaciones personalizadas
   */
  public RecommendationResponse getRecommendationsForMember(Long memberId) {

    log.info("🤖 Generando recomendaciones automáticas para member: {}", memberId);

    // 1. Obtener datos del miembro desde BD
    Member member = memberService.findById(memberId);

    // 2. Obtener planes elegibles desde BD
    List<InsurancePlan> eligiblePlans = planService.getEligiblePlansForMember(member);

    log.info("📋 Planes elegibles para member {}: {} planes",
            memberId, eligiblePlans.size());

    if (eligiblePlans.isEmpty()) {
      log.warn("⚠️  No hay planes elegibles para member: {}", memberId);
      return buildNoPlansAvailableResponse(member);
    }

    // 3. Construir prompt con datos de BD
    try {
      String systemPrompt = promptService.buildSystemPrompt();
      String userPrompt = promptService.buildUserPromptFromDB(member, eligiblePlans);

      List<Message> messages = List.of(
              new SystemMessage(systemPrompt),
              new UserMessage(userPrompt)
      );

      // 4. Llamar a IA
      Prompt prompt = new Prompt(messages);
      String aiResponse = chatModel.call(prompt)
              .getResult()
              .getOutput()
              .getContent();

      log.debug("✅ Respuesta de IA recibida para member: {}", memberId);

      // 5. Parsear y enriquecer respuesta
      RecommendationResponse response = parseAIResponse(aiResponse);
      enrichResponse(response, member);

      log.info("🎯 Recomendaciones generadas exitosamente para member: {}", memberId);
      return response;

    } catch (Exception e) {
      log.error("❌ Error generando recomendaciones con IA para member: {}", memberId, e);
      throw new AIRecommendationException(
              "Error al procesar recomendaciones con IA", e);
    }
  }

  /**
   * Enriquece la respuesta con datos del miembro
   */
  private void enrichResponse(RecommendationResponse response, Member member) {
    response.setMemberId(member.getId());
    response.setMemberName(String.format("- Nombre: %s\n", member.getFirstName() + " " + member.getLastName()));
    response.setMemberEmail(member.getEmail());
    response.setGeneratedAt(LocalDateTime.now());
    response.setModelUsed("google/gemini-2.0-flash-exp");
    response.setFromCache(false);
  }

  /**
   * Parsea la respuesta JSON de la IA
   */
  private RecommendationResponse parseAIResponse(String aiResponse) {
    try {
      String cleanJson = aiResponse.trim()
              .replaceFirst("^```json\\s*", "")
              .replaceFirst("^```\\s*", "")
              .replaceFirst("\\s*```$", "")
              .trim();

      JsonNode root = objectMapper.readTree(cleanJson);

      List<PlanRecommendation> recommendations = new ArrayList<>();
      JsonNode recsNode = root.get("recommendations");
      if (recsNode != null && recsNode.isArray()) {
        for (JsonNode recNode : recsNode) {
          PlanRecommendation rec = objectMapper.treeToValue(
                  recNode, PlanRecommendation.class);
          recommendations.add(rec);
        }
      }

      String generalAnalysis = root.has("generalAnalysis")
              ? root.get("generalAnalysis").asText()
              : "Análisis no disponible";

      List<String> riskFactors = new ArrayList<>();
      JsonNode riskNode = root.get("riskFactors");
      if (riskNode != null && riskNode.isArray()) {
        riskNode.forEach(node -> riskFactors.add(node.asText()));
      }

      List<String> healthTips = new ArrayList<>();
      JsonNode tipsNode = root.get("healthTips");
      if (tipsNode != null && tipsNode.isArray()) {
        tipsNode.forEach(node -> healthTips.add(node.asText()));
      }

      return RecommendationResponse.builder()
              .recommendations(recommendations)
              .generalAnalysis(generalAnalysis)
              .riskFactors(riskFactors)
              .healthTips(healthTips)
              .build();

    } catch (Exception e) {
      log.error("Error parseando respuesta de IA", e);
      throw new AIRecommendationException("Error al parsear respuesta de IA", e);
    }
  }

  /**
   * Respuesta cuando no hay planes disponibles
   */
  private RecommendationResponse buildNoPlansAvailableResponse(Member member) {
    return RecommendationResponse.builder()
            .memberId(member.getId())
            .memberName(String.format("- Nombre: %s\n", member.getFirstName() + " " + member.getLastName()))
            .memberEmail(member.getEmail())
            .recommendations(List.of())
            .generalAnalysis("Actualmente no hay planes disponibles que cumplan con los criterios de elegibilidad para este perfil.")
            .riskFactors(List.of("No se pudo realizar análisis de riesgo"))
            .healthTips(List.of("Contacte a un asesor para opciones personalizadas"))
            .generatedAt(LocalDateTime.now())
            .modelUsed("system")
            .fromCache(false)
            .build();
  }

  /**
   * Fallback cuando IA falla
   */
  public RecommendationResponse getDefaultRecommendations(Long memberId, Exception ex) {

    log.warn("⚠️  Usando fallback para member: {} debido a: {}",
            memberId, ex.getMessage());

    Member member = memberService.findById(memberId);

    List<InsurancePlan> eligiblePlans = planService.getEligiblePlansForMember(member);

    // Convertir planes de BD a recomendaciones simples
    List<PlanRecommendation> recommendations = eligiblePlans.stream()
            .limit(3)
            .map(this::convertPlanToRecommendation)
            .collect(Collectors.toList());

    return RecommendationResponse.builder()
            .memberId(member.getId())
            .memberName(String.format("- Nombre: %s\n", member.getFirstName() + " " + member.getLastName()))
            .memberEmail(member.getEmail())
            .recommendations(recommendations)
            .generalAnalysis("Recomendaciones basadas en criterios de elegibilidad (IA temporalmente no disponible)")
            .riskFactors(List.of("Evaluación detallada no disponible temporalmente"))
            .healthTips(List.of("Consulte con un asesor para análisis personalizado"))
            .generatedAt(LocalDateTime.now())
            .modelUsed("fallback-rules")
            .fromCache(false)
            .build();
  }

  /**
   * Convierte un plan de BD a recomendación simple
   */
  private PlanRecommendation convertPlanToRecommendation(InsurancePlan plan) {
    return PlanRecommendation.builder()
            .planId(plan.getPlanId())
            .planName(plan.getPlanName())
            .planType(plan.getPlanType())
            .recommendationScore(70) // Score genérico
            .monthlyPremium(plan.getMonthlyPremium())
            .annualDeductible(plan.getAnnualDeductible())
            .coverageLevel(plan.getCoverageLevel())
            .includedBenefits(plan.getIncludedBenefits())
            .exclusions(plan.getExclusions())
            .justification(plan.getDescription())
            .pros(List.of("Plan elegible según criterios básicos"))
            .cons(List.of("Análisis detallado no disponible"))
            .budgetFit("UNKNOWN")
            .build();
  }
}
