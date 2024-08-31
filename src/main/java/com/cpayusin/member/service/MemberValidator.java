package com.cpayusin.member.service;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.service.port.MemberRepository;
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

    public void validateNickname(long memberId, String nickname)
    {
        if(memberRepository.existsByNickname(memberId, nickname)){
            throw new BusinessLogicException(ExceptionMessage.NICKNAME_ALREADY_EXIST);
        }
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
