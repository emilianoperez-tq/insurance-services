package ar.com.smg.member_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "members")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer age;

  @Column(name = "first_name", nullable = false)
  private String firstName;
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "dni", unique = true, nullable = false)
  private String dni;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(length = 20)
  private String gender; // MALE, FEMALE, OTHER

  @Column(length = 20)
  private String maritalStatus; // SINGLE, MARRIED, DIVORCED, WIDOWED

  @Column
  private Integer numberOfDependents;

  @Column(length = 20)
  private String incomeLevel; // LOW, MEDIUM, HIGH

  // Historial médico (almacenado como JSON o lista separada por comas)
  @ElementCollection
  @CollectionTable(name = "member_pre_existing_conditions",
          joinColumns = @JoinColumn(name = "member_id"))
  @Column(name = "condition_name")
  private List<String> preExistingConditions = new ArrayList<>();

  @Column
  private Boolean smoker;

  @Column(length = 20)
  private String physicalActivityLevel; // SEDENTARY, MODERATE, ACTIVE

  @Column
  private Double monthlyBudget;

  @Column(length = 100)
  private String occupation;

  @Column
  private Boolean hasExistingInsurance;

  @Column(columnDefinition = "TEXT")
  private String additionalNotes;

  // Timestamps
  @Column(updatable = false)
  private LocalDate createdAt;

  @Column
  private LocalDate updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDate.now();
    updatedAt = LocalDate.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDate.now();
  }
}