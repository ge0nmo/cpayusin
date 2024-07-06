package com.jbaacount.service;

import com.jbaacount.config.TestContainerExtension;
import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.model.Member;
import com.jbaacount.payload.response.member.MemberDetailResponse;
import com.jbaacount.repository.MemberRepository;
import com.jbaacount.setup.MockSetup;
import com.jbaacount.config.TearDownExtension;
import com.jbaacount.validator.MemberValidator;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TestContainerExtension.class)
@ExtendWith(TearDownExtension.class)
@SpringBootTest
class MemberServiceTest extends MockSetup
{
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberValidator memberValidator;


    @BeforeEach
    void beforeEach()
    {
        memberRepository.save(mockMember);
    }

    @DisplayName("Delete test")
    @Test
    void deleteById()
    {
        // given
        Long id = mockMember.getId();

        // when
        Optional<Member> beforeDelete = memberRepository.findById(id);

        boolean result = memberService.deleteById(mockMember);

        // then
        Optional<Member> afterDelete = memberRepository.findById(id);

        assertThat(beforeDelete).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }

    @DisplayName("Check if email exists after deletion")
    @Test
    void checkEmailExist()
    {
        // given
        Long id = mockMember.getId();
        Member beforeDelete = memberRepository.findById(id).get();
        String email = beforeDelete.getEmail();

        // when


        String result = memberValidator.checkExistEmail(email);
        assertThat(result).isEqualTo("이미 사용중인 이메일입니다.");

        memberService.deleteById(mockMember);

        // then
        result = memberValidator.checkExistEmail(email);
        assertThat(result).isEqualTo("사용할 수 있는 이메일입니다.");
    }

    @DisplayName("Check if nickname exists after deletion")
    @Test
    void checkNicknameExist()
    {
        // given
        Long id = mockMember.getId();
        Member beforeDelete = memberRepository.findById(id).get();
        String nickname = beforeDelete.getNickname();

        // when


        String result = memberValidator.checkExistNickname(nickname);
        assertThat(result).isEqualTo("이미 사용중인 닉네임입니다.");

        memberService.deleteById(mockMember);

        // then
        result = memberValidator.checkExistNickname(nickname);
        assertThat(result).isEqualTo("사용할 수 있는 닉네임입니다.");
    }

    @DisplayName("Find email test")
    @Test
    void findMemberByEmail1()
    {
        // given
        String email = mockMember.getEmail();

        // when
        Member foundMember = memberService.findMemberByEmail(email);

        // then
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getEmail()).isEqualTo(email);
    }

    @DisplayName("Find email test - throw error when email doesn't exist")
    @Test
    void findMemberByEmail2()
    {
        // given
        String email = "unknown@gmail.com";

        // when

        // then
        assertThrows(BusinessLogicException.class, () -> memberService.findMemberByEmail(email));
    }

    @Test
    void getMemberDetailResponse()
    {
        // given
        Long id = mockMember.getId();

        // when
        MemberDetailResponse result = memberService.getMemberDetailResponse(id);

        // then
        assertThat(result.getNickname()).isEqualTo("test");
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
    }


}