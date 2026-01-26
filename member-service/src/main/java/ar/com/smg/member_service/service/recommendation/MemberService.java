package ar.com.smg.member_service.service.recommendation;

import ar.com.smg.member_service.model.entity.Member;
import ar.com.smg.member_service.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class MemberService {

  private final MemberRepository memberRepository;

  public Member findById(Long memberId) {
    return memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
  }

}
