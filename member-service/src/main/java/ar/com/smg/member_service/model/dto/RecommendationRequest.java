package ar.com.smg.member_service.model.dto;

import ar.com.smg.member_service.model.entity.Member;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

  // Estos campos vienen de Member entity automáticamente
  private Long memberId;
  private Integer age;
  private String email;

  // ========== CAMPOS OPCIONALES (pueden venir del request) ==========

  private String gender; // MALE, FEMALE, OTHER

  private String maritalStatus; // SINGLE, MARRIED, DIVORCED, WIDOWED

  private Integer numberOfDependents;

  @NotBlank(message = "Nivel de ingresos es requerido")
  private String incomeLevel; // LOW, MEDIUM, HIGH

  // Historial médico
  private List<String> preExistingConditions;

  private Boolean smoker;

  private String physicalActivityLevel; // SEDENTARY, MODERATE, ACTIVE

  // Preferencias
  private String coveragePreference; // BASIC, COMPREHENSIVE, PREMIUM

  private List<String> priorityBenefits;

  @DecimalMin(value = "0.0", message = "Presupuesto debe ser mayor a 0")
  private Double monthlyBudget;

  // Contexto adicional
  private String occupation;

  private Boolean hasExistingInsurance;

  private String additionalNotes;

  /**
   * Factory method para crear request desde Member entity
   */
  public static RecommendationRequest fromMember(Member member) {
    return RecommendationRequest.builder()
            .memberId(member.getId())
            .age(member.getAge())
            .email(member.getEmail())
            // Otros campos se pueden mapear si existen en Member
            .build();
  }
}
