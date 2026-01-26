package ar.com.smg.member_service.service.recommendation;

import ar.com.smg.member_service.model.dto.RecommendationRequest;
import ar.com.smg.member_service.model.entity.InsurancePlan;
import ar.com.smg.member_service.model.entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptService {

  public String buildSystemPrompt() {
    return """
            Eres un asesor experto en seguros de salud con 15 años de experiencia.
            Tu misión es analizar el perfil de un cliente y recomendar los planes de seguro más adecuados del catálogo disponible.
            
            DIRECTRICES:
            1. Analiza TODOS los factores: edad, condiciones preexistentes, presupuesto, preferencias
            2. Prioriza la SALUD del cliente sobre las ventas
            3. Sé HONESTO sobre limitaciones y exclusiones
            4. Explica en lenguaje claro, sin jerga técnica excesiva
            5. SOLO recomienda planes del catálogo proporcionado
            6. Proporciona justificaciones basadas en el match entre perfil y beneficios del plan
            
            ESTRUCTURA DE RESPUESTA REQUERIDA (JSON):
            {
              "generalAnalysis": "Análisis general del perfil (2-3 oraciones)",
              "riskFactors": ["factor1", "factor2"],
              "healthTips": ["consejo1", "consejo2"],
              "recommendations": [
                {
                  "planId": "PLAN_XXX",
                  "planName": "Nombre del plan del catálogo",
                  "planType": "BASIC|STANDARD|PREMIUM|COMPREHENSIVE",
                  "recommendationScore": 85,
                  "monthlyPremium": 350.00,
                  "annualDeductible": 2000.00,
                  "coverageLevel": "80%",
                  "includedBenefits": ["beneficio1", "beneficio2"],
                  "exclusions": ["exclusion1"],
                  "justification": "Por qué este plan específico es ideal para este perfil",
                  "pros": ["pro1", "pro2"],
                  "cons": ["con1"],
                  "budgetFit": "EXCELLENT|GOOD|TIGHT|OVER_BUDGET",
                  "comparisonWithCurrent": "Si aplica"
                }
              ]
            }
            
            IMPORTANTE: 
            - Responde SOLO con JSON válido, sin texto adicional
            - USA EXACTAMENTE los planId del catálogo proporcionado
            - NO inventes planes que no estén en el catálogo
            - Ordena las recomendaciones por recommendationScore (mayor a menor)
            - Recomienda máximo 3 planes
            """;
  }

  /**
   * Construye el prompt del usuario desde datos de BD
   * Incluye perfil del miembro Y catálogo de planes disponibles
   */
  public String buildUserPromptFromDB(Member member, List<InsurancePlan> availablePlans) {
    StringBuilder prompt = new StringBuilder();

    prompt.append("=== PERFIL DEL CLIENTE ===\n\n");

    // Datos demográficos
    prompt.append("INFORMACIÓN DEMOGRÁFICA:\n");
    prompt.append(String.format("- ID: %d\n", member.getId()));
    prompt.append(String.format("- Nombre: %s\n", member.getFirstName() + " " + member.getLastName()));
    prompt.append(String.format("- Email: %s\n", member.getEmail()));
    prompt.append(String.format("- Edad: %d años\n", member.getAge()));

    if (member.getGender() != null) {
      prompt.append(String.format("- Género: %s\n", member.getGender()));
    }

    if (member.getMaritalStatus() != null) {
      prompt.append(String.format("- Estado civil: %s\n", member.getMaritalStatus()));
    }

    if (member.getNumberOfDependents() != null && member.getNumberOfDependents() > 0) {
      prompt.append(String.format("- Dependientes: %d\n", member.getNumberOfDependents()));
    }

    if (member.getIncomeLevel() != null) {
      prompt.append(String.format("- Nivel de ingresos: %s\n", member.getIncomeLevel()));
    }

    if (member.getOccupation() != null) {
      prompt.append(String.format("- Ocupación: %s\n", member.getOccupation()));
    }

    // Historial médico
    prompt.append("\nHISTORIAL MÉDICO Y SALUD:\n");

    if (member.getPreExistingConditions() != null && !member.getPreExistingConditions().isEmpty()) {
      prompt.append("- Condiciones preexistentes: ");
      prompt.append(String.join(", ", member.getPreExistingConditions()));
      prompt.append("\n");
    } else {
      prompt.append("- Sin condiciones preexistentes registradas\n");
    }

    prompt.append(String.format("- Fumador: %s\n",
            member.getSmoker() != null && member.getSmoker() ? "Sí" : "No"));

    if (member.getPhysicalActivityLevel() != null) {
      prompt.append(String.format("- Nivel de actividad física: %s\n",
              member.getPhysicalActivityLevel()));
    }

    // Preferencias y presupuesto
    prompt.append("\nPREFERENCIAS Y PRESUPUESTO:\n");

    if (member.getMonthlyBudget() != null) {
      prompt.append(String.format("- Presupuesto mensual: $%.2f\n",
              member.getMonthlyBudget()));
    }

    if (member.getHasExistingInsurance() != null && member.getHasExistingInsurance()) {
      prompt.append("- Actualmente tiene seguro (considerar comparación)\n");
    }

    if (member.getAdditionalNotes() != null && !member.getAdditionalNotes().isBlank()) {
      prompt.append(String.format("\nNOTAS ADICIONALES DEL CLIENTE:\n%s\n",
              member.getAdditionalNotes()));
    }

    // Catálogo de planes disponibles
    prompt.append("\n\n=== CATÁLOGO DE PLANES DISPONIBLES ===\n\n");
    prompt.append(String.format("Total de planes elegibles: %d\n\n", availablePlans.size()));

    for (int i = 0; i < availablePlans.size(); i++) {
      InsurancePlan plan = availablePlans.get(i);
      prompt.append(String.format("PLAN %d:\n", i + 1));
      prompt.append(String.format("- Plan ID: %s\n", plan.getPlanId()));
      prompt.append(String.format("- Nombre: %s\n", plan.getPlanName()));
      prompt.append(String.format("- Tipo: %s\n", plan.getPlanType()));
      prompt.append(String.format("- Prima mensual: $%.2f\n", plan.getMonthlyPremium()));
      prompt.append(String.format("- Deducible anual: $%.2f\n", plan.getAnnualDeductible()));
      prompt.append(String.format("- Nivel de cobertura: %s\n", plan.getCoverageLevel()));

      if (plan.getDescription() != null) {
        prompt.append(String.format("- Descripción: %s\n", plan.getDescription()));
      }

      if (plan.getIncludedBenefits() != null && !plan.getIncludedBenefits().isEmpty()) {
        prompt.append("- Beneficios incluidos:\n");
        for (String benefit : plan.getIncludedBenefits()) {
          prompt.append(String.format("  • %s\n", benefit));
        }
      }

      if (plan.getExclusions() != null && !plan.getExclusions().isEmpty()) {
        prompt.append("- Exclusiones:\n");
        for (String exclusion : plan.getExclusions()) {
          prompt.append(String.format("  • %s\n", exclusion));
        }
      }

      if (plan.getMinAge() != null || plan.getMaxAge() != null) {
        prompt.append("- Rango de edad: ");
        if (plan.getMinAge() != null) {
          prompt.append(String.format("%d", plan.getMinAge()));
        }
        prompt.append(" - ");
        if (plan.getMaxAge() != null) {
          prompt.append(String.format("%d", plan.getMaxAge()));
        } else {
          prompt.append("sin límite");
        }
        prompt.append(" años\n");
      }

      if (plan.getAllowsSmokers() != null) {
        prompt.append(String.format("- Acepta fumadores: %s\n",
                plan.getAllowsSmokers() ? "Sí" : "No"));
      }

      prompt.append("\n");
    }

    prompt.append("=== FIN DEL CATÁLOGO ===\n\n");

    prompt.append("INSTRUCCIONES FINALES:\n");
    prompt.append("1. Analiza cuidadosamente el perfil del cliente\n");
    prompt.append("2. Compara TODOS los planes del catálogo\n");
    prompt.append("3. Selecciona los 3 mejores planes que se ajusten al perfil\n");
    prompt.append("4. Asigna recommendationScore (0-100) según el match\n");
    prompt.append("5. Justifica POR QUÉ cada plan es adecuado para este cliente específico\n");
    prompt.append("6. Considera presupuesto, condiciones de salud y preferencias\n");
    prompt.append("7. USA EXACTAMENTE los planId del catálogo (no inventes)\n");
    prompt.append("\nResponde SOLO con JSON válido siguiendo la estructura especificada.\n");

    return prompt.toString();
  }
}
