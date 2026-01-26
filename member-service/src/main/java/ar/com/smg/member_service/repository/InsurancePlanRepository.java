package ar.com.smg.member_service.repository;

import ar.com.smg.member_service.model.entity.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {

  Optional<InsurancePlan> findByPlanId(String planId);

  List<InsurancePlan> findByActiveTrue();

  List<InsurancePlan> findByPlanType(String planType);

  /**
   * Encuentra planes elegibles para un miembro según edad
   */
  @Query("SELECT p FROM InsurancePlan p WHERE p.active = true " +
          "AND (p.minAge IS NULL OR p.minAge <= :age) " +
          "AND (p.maxAge IS NULL OR p.maxAge >= :age)")
  List<InsurancePlan> findEligiblePlansForAge(@Param("age") Integer age);

  /**
   * Encuentra planes que permiten fumadores o que no tienen restricción
   */
  @Query("SELECT p FROM InsurancePlan p WHERE p.active = true " +
          "AND (p.allowsSmokers = true OR p.allowsSmokers IS NULL)")
  List<InsurancePlan> findPlansAllowingSmokers();
}
