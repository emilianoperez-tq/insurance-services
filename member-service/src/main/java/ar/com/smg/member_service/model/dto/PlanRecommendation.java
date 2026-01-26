package ar.com.smg.member_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanRecommendation {

  private String planId;
  private String planName;
  private String planType;

  /**
   * Score 0-100
   */
  private Integer recommendationScore;

  private BigDecimal monthlyPremium;
  private BigDecimal annualDeductible;
  private String coverageLevel;

  private List<String> includedBenefits;
  private List<String> exclusions;

  private String justification;
  private List<String> pros;
  private List<String> cons;

  private String budgetFit;
  private String comparisonWithCurrent;
}
