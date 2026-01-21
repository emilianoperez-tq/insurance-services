package ar.com.smg.member_service.controller;

import ar.com.smg.member_service.entity.Member;
import ar.com.smg.member_service.repository.MemberRepository;
import arg.com.smg.audit_client.client.AuditClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

  private MemberRepository memberRepository;
  private AuditClient auditClient;

  public MemberController(MemberRepository memberRepository, AuditClient auditClient) {
    this.memberRepository = memberRepository;
    this.auditClient = auditClient;
  }

  @PostMapping
  public Member createMember(@RequestBody Member member) {
    return memberRepository.save(member);
  }

  @GetMapping("/{id}")
  public Member getMember(@PathVariable Long id) {
    PutItemResponse response = auditClient.log("member-controller", "getMember", "Fetching member with id: " + id,
            "INFO");
    log.info(response.toString());

    return memberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found"));
  }

  @GetMapping
  public List<Member> getAllMembers() {
    return memberRepository.findAll();
  }

  @GetMapping("/test")
  public String testEndpoint() {
    return "Member Service is up and running!";
  }
}
