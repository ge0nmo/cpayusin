package com.cpayusin.service;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.config.TearDownExtension;
import com.cpayusin.config.TestContainerExtension;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.infrastructure.MemberEntity;
import com.cpayusin.member.service.MemberValidator;
import com.cpayusin.member.service.port.MemberRepository;
import com.cpayusin.setup.MockSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        memberRepository.save(mockMemberEntity);
    }

    @DisplayName("Delete test")
    @Test
    void deleteById()
    {
        // given
        Long id = mockMemberEntity.getId();

        // when
        Optional<MemberEntity> beforeDelete = memberRepository.findById(id);

        boolean result = memberService.deleteById(mockMemberEntity);

        // then
        Optional<MemberEntity> afterDelete = memberRepository.findById(id);

        assertThat(beforeDelete).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }

    @DisplayName("Check if email exists after deletion")
    @Test
    void checkEmailExist()
    {
        // given
        Long id = mockMemberEntity.getId();
        MemberEntity beforeDelete = memberRepository.findById(id).get();
        String email = beforeDelete.getEmail();

        // when


        String result = memberValidator.checkExistEmail(email);
        assertThat(result).isEqualTo("이미 사용중인 이메일입니다.");

        memberService.deleteById(mockMemberEntity);

        // then
        result = memberValidator.checkExistEmail(email);
        assertThat(result).isEqualTo("사용할 수 있는 이메일입니다.");
    }

    @DisplayName("Check if nickname exists after deletion")
    @Test
    void checkNicknameExist()
    {
        // given
        Long id = mockMemberEntity.getId();
        MemberEntity beforeDelete = memberRepository.findById(id).get();
        String nickname = beforeDelete.getNickname();

        // when


        String result = memberValidator.checkExistNickname(nickname);
        assertThat(result).isEqualTo("이미 사용중인 닉네임입니다.");

        memberService.deleteById(mockMemberEntity);

        // then
        result = memberValidator.checkExistNickname(nickname);
        assertThat(result).isEqualTo("사용할 수 있는 닉네임입니다.");
    }

    @DisplayName("Find email test")
    @Test
    void findMemberByEmail1()
    {
        // given
        String email = mockMemberEntity.getEmail();

        // when
        MemberEntity foundMemberEntity = memberService.findMemberByEmail(email);

        // then
        assertThat(foundMemberEntity).isNotNull();
        assertThat(foundMemberEntity.getEmail()).isEqualTo(email);
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
        Long id = mockMemberEntity.getId();

        // when
        MemberDetailResponse result = memberService.getMemberDetailResponse(id);

        // then
        assertThat(result.getNickname()).isEqualTo("test");
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
    }


}