package com.cpayusin.global.security.userdetails;

import com.cpayusin.global.exception.BusinessLogicException;
import com.cpayusin.global.exception.ExceptionMessage;
import com.cpayusin.model.Member;
import com.cpayusin.repository.MemberRepository;
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
