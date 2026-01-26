package ar.com.smg.member_service.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Catálogo de planes de seguro disponibles
 */
@Entity
@Table(name = "insurance_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String planId; // Ej: "BASIC_001", "PREMIUM_003"

  @Column(nullable = false, length = 100)
  private String planName;

  @Column(nullable = false, length = 30)
  private String planType; // BASIC, STANDARD, PREMIUM, COMPREHENSIVE

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal monthlyPremium;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal annualDeductible;

  @Column(length = 10)
  private String coverageLevel; // "60%", "80%", "95%"

  @ElementCollection
  @CollectionTable(name = "plan_included_benefits",
          joinColumns = @JoinColumn(name = "plan_id"))
  @Column(name = "benefit_name")
  private List<String> includedBenefits = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "plan_exclusions",
          joinColumns = @JoinColumn(name = "plan_id"))
  @Column(name = "exclusion_name")
  private List<String> exclusions = new ArrayList<>();

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column
  private Boolean active = true; // Para desactivar planes sin borrarlos

  // Criterios de elegibilidad (opcional)
  @Column
  private Integer minAge;

  @Column
  private Integer maxAge;

  @Column
  private Boolean allowsSmokers;
}
