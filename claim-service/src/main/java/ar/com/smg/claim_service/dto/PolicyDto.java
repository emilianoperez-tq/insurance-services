package ar.com.smg.claim_service.dto;

import lombok.Data;

@Data
public class PolicyDto {
  private Long id;
  private Long memberId;
  private Integer policyNumber;
  private String type;
}
