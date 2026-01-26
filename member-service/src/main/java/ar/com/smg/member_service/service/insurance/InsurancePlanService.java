package ar.com.smg.member_service.service.insurance;

import ar.com.smg.member_service.model.entity.InsurancePlan;
import ar.com.smg.member_service.model.entity.Member;
import ar.com.smg.member_service.repository.InsurancePlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsurancePlanService implements CommandLineRunner {

  private final InsurancePlanRepository planRepository;

  /**
   * Obtiene todos los planes activos
   */
  public List<InsurancePlan> getAllActivePlans() {
    return planRepository.findByActiveTrue();
  }

  /**
   * Obtiene planes elegibles para un miembro específico
   * Considera: edad, si es fumador, etc.
   */
  public List<InsurancePlan> getEligiblePlansForMember(Member member) {
    List<InsurancePlan> eligiblePlans = planRepository.findEligiblePlansForAge(member.getAge());

    // Si es fumador, filtrar solo planes que permiten fumadores
    if (Boolean.TRUE.equals(member.getSmoker())) {
      eligiblePlans = eligiblePlans.stream()
              .filter(plan -> plan.getAllowsSmokers() == null || plan.getAllowsSmokers())
              .toList();
    }

    return eligiblePlans;
  }

  /**
   * Inicializa planes de ejemplo si la BD está vacía
   * Se ejecuta al iniciar la aplicación
   */
  @Override
  @Transactional
  public void run(String... args) {
    if (planRepository.count() == 0) {
      log.info("🏥 Inicializando catálogo de planes de seguro...");
      initializeSamplePlans();
      log.info("✅ Planes inicializados: {} planes creados", planRepository.count());
    } else {
      log.info("📋 Catálogo de planes ya existe: {} planes disponibles",
              planRepository.count());
    }
  }

  /**
   * Crea planes de ejemplo para testing
   */
  private void initializeSamplePlans() {
    // Plan Básico
    InsurancePlan basicPlan = InsurancePlan.builder()
            .planId("BASIC_001")
            .planName("Plan Básico Essential")
            .planType("BASIC")
            .monthlyPremium(BigDecimal.valueOf(250.00))
            .annualDeductible(BigDecimal.valueOf(3000.00))
            .coverageLevel("60%")
            .includedBenefits(List.of(
                    "Consultas médicas generales",
                    "Atención de emergencias 24/7",
                    "Medicamentos básicos con receta",
                    "Laboratorios básicos",
                    "Rayos X"
            ))
            .exclusions(List.of(
                    "Tratamientos odontológicos",
                    "Oftalmología",
                    "Cirugías estéticas",
                    "Tratamientos experimentales"
            ))
            .description("Plan económico ideal para jóvenes saludables sin condiciones preexistentes")
            .minAge(18)
            .maxAge(35)
            .allowsSmokers(true)
            .active(true)
            .build();

    // Plan Estándar
    InsurancePlan standardPlan = InsurancePlan.builder()
            .planId("STANDARD_002")
            .planName("Plan Estándar Complete")
            .planType("STANDARD")
            .monthlyPremium(BigDecimal.valueOf(450.00))
            .annualDeductible(BigDecimal.valueOf(1500.00))
            .coverageLevel("80%")
            .includedBenefits(List.of(
                    "Todas las consultas médicas",
                    "Emergencias 24/7",
                    "Medicamentos con 70% de descuento",
                    "Laboratorios e imagenología completa",
                    "Consultas con especialistas",
                    "Hospitalización",
                    "Cirugías no estéticas"
            ))
            .exclusions(List.of(
                    "Tratamientos estéticos",
                    "Odontología cosmética",
                    "Fertilización in vitro"
            ))
            .description("Balance perfecto entre cobertura y precio para familias")
            .minAge(18)
            .maxAge(60)
            .allowsSmokers(true)
            .active(true)
            .build();

    // Plan Premium
    InsurancePlan premiumPlan = InsurancePlan.builder()
            .planId("PREMIUM_003")
            .planName("Plan Premium Total Care")
            .planType("PREMIUM")
            .monthlyPremium(BigDecimal.valueOf(750.00))
            .annualDeductible(BigDecimal.valueOf(500.00))
            .coverageLevel("95%")
            .includedBenefits(List.of(
                    "Cobertura médica completa",
                    "Todas las especialidades",
                    "Odontología completa",
                    "Oftalmología y lentes",
                    "Maternidad y neonatología",
                    "Medicina preventiva",
                    "Terapias alternativas",
                    "Segunda opinión médica",
                    "Medicamentos al 90%"
            ))
            .exclusions(List.of(
                    "Tratamientos puramente estéticos no médicos"
            ))
            .description("Cobertura premium sin límites para tranquilidad total")
            .minAge(18)
            .maxAge(null) // Sin límite de edad
            .allowsSmokers(false) // No acepta fumadores
            .active(true)
            .build();

    // Plan Familiar Integral
    InsurancePlan comprehensivePlan = InsurancePlan.builder()
            .planId("COMPREHENSIVE_FAMILY_001")
            .planName("Plan Familiar Integral Plus")
            .planType("COMPREHENSIVE")
            .monthlyPremium(BigDecimal.valueOf(580.00))
            .annualDeductible(BigDecimal.valueOf(1000.00))
            .coverageLevel("90%")
            .includedBenefits(List.of(
                    "Cobertura familiar completa",
                    "Atención especializada para condiciones crónicas",
                    "Endocrinología (diabetes, tiroides)",
                    "Cardiología preventiva y seguimiento",
                    "Nutrición y educación en salud",
                    "Medicina familiar sin límite de consultas",
                    "Cobertura deportiva para menores",
                    "Medicamentos crónicos con 80% descuento",
                    "Telemedicina 24/7"
            ))
            .exclusions(List.of(
                    "Tratamientos estéticos",
                    "Cirugías experimentales"
            ))
            .description("Ideal para familias con necesidades especiales de salud y condiciones crónicas")
            .minAge(25)
            .maxAge(65)
            .allowsSmokers(true)
            .active(true)
            .build();

    // Plan Senior
    InsurancePlan seniorPlan = InsurancePlan.builder()
            .planId("SENIOR_004")
            .planName("Plan Senior Care")
            .planType("PREMIUM")
            .monthlyPremium(BigDecimal.valueOf(850.00))
            .annualDeductible(BigDecimal.valueOf(800.00))
            .coverageLevel("92%")
            .includedBenefits(List.of(
                    "Geriatría especializada",
                    "Enfermedades crónicas",
                    "Rehabilitación física",
                    "Atención domiciliaria",
                    "Cuidados paliativos",
                    "Medicamentos sin límite",
                    "Chequeos preventivos trimestrales",
                    "Asistencia de enfermería"
            ))
            .exclusions(List.of(
                    "Tratamientos estéticos"
            ))
            .description("Especializado en adultos mayores con atención integral")
            .minAge(60)
            .maxAge(null)
            .allowsSmokers(true)
            .active(true)
            .build();

    // Guardar todos
    planRepository.saveAll(List.of(
            basicPlan,
            standardPlan,
            premiumPlan,
            comprehensivePlan,
            seniorPlan
    ));
  }
}
