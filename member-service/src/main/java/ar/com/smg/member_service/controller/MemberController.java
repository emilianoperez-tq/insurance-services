package ar.com.smg.member_service.controller;

import ar.com.smg.member_service.entity.Member;
import ar.com.smg.member_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

  private MemberRepository memberRepository;

  public MemberController(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @PostMapping
  public Member createMember(@RequestBody Member member) {
    return memberRepository.save(member);
  }

  @GetMapping("/{id}")
  public Member getMember(@PathVariable Long id) {
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
