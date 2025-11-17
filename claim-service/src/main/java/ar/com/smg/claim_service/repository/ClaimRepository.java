package ar.com.smg.claim_service.repository;

import ar.com.smg.claim_service.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
