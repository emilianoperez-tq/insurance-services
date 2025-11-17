package ar.com.smg.policy_service.controller;

import ar.com.smg.policy_service.entity.Policy;
import ar.com.smg.policy_service.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policies")
public class PolicyController {

  private final PolicyRepository policyRepository;

  @PostMapping
  public Policy createPolicy(@RequestBody Policy policy) {
    return policyRepository.save(policy);
  }

  @GetMapping("/member/{memberId}")
  public Policy getPolicyByMemberId(@PathVariable Long memberId) {
    return policyRepository.findAll().stream()
            .filter(policy -> policy.getMemberId().equals(memberId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Policy not found for memberId: " + memberId));
  }

  @GetMapping("/{id}")
  public Policy getPolicy(@PathVariable Long id) {
    return policyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Policy not found"));
  }
}
