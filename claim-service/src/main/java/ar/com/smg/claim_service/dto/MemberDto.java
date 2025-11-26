package ar.com.smg.claim_service.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
  private Long id;
  private Integer age;
  private String firstName;
  private String lastName;
  private String dni;
  private String email;
}
