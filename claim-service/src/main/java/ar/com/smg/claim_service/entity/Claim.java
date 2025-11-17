package ar.com.smg.claim_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "claims")
public class Claim {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "policy_id", nullable = false)
  private Long policyId;
  @Column(name = "amount", nullable = false)
  private Double amount;
  @Column(nullable = true)
  private String imageUrl;

  @Column(nullable = false)
  private String status;
}
