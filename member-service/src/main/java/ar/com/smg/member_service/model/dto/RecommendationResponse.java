package ar.com.smg.member_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

  private Long memberId;
  private String memberName;
  private String memberEmail;

  private List<PlanRecommendation> recommendations;

  private String generalAnalysis;

  private List<String> riskFactors;

  private List<String> healthTips;

  private LocalDateTime generatedAt;

  private String modelUsed;

  private Boolean fromCache;
}
