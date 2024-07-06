package com.jbaacount.validator;

import com.jbaacount.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberValidator
{
    private final MemberRepository memberRepository;

    public boolean verifyExistEmail(String email)
    {
        return memberRepository.existsByEmail(email);
    }
    
    public boolean verifyExistNickname(String nickname)
    {
        return memberRepository.existsByNickname(nickname);
    }

    public String checkExistEmail(String email)
    {
        boolean response = memberRepository.findByEmail(email).isPresent();

        return response ? "이미 사용중인 이메일입니다." : "사용할 수 있는 이메일입니다.";
    }

    public String checkExistNickname(String nickname)
    {
        boolean response = memberRepository.findByNickname(nickname).isPresent();

        return response ? "이미 사용중인 닉네임입니다." : "사용할 수 있는 닉네임입니다.";
    }

}
