package com.jbaacount.service;

import com.jbaacount.dummy.DummyObject;
import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.security.userdetails.MemberDetails;
import com.jbaacount.global.security.userdetails.MemberDetailsService;
import com.jbaacount.model.Member;
import com.jbaacount.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberDetailsServiceTest extends DummyObject
{
    @InjectMocks
    private MemberDetailsService memberDetailsService;

    @Mock
    private MemberRepository memberRepository;


    private Member member;

    @BeforeEach
    void setUp()
    {
        member = newMockMember(1L, "test@gmail.com", "test", "ADMIN");
    }

    @Test
    void loadByUsername()
    {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        MemberDetails memberDetails = memberDetailsService.loadUserByUsername(member.getEmail());

        // then
        assertThat(memberDetails).isNotNull();
        assertThat(memberDetails.getMember()).isEqualTo(member);
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @DisplayName("should throw Business Logic Exception")
    @Test
    void loadByUsernameWrongEmail()
    {
        // given

        // when

        assertThrows(BusinessLogicException.class, () -> memberDetailsService.loadUserByUsername(member.getEmail()));
        // then
        verify(memberRepository, times(1)).findByEmail(anyString());
    }
}
