package com.cpayusin.common.security.userdetails;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.domain.Member;
import com.cpayusin.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberDetailsService implements UserDetailsService
{
    private final MemberRepository memberRepository;

    @Override
    public MemberDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));

        return new MemberDetails(member);
    }
}
