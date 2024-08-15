package com.cpayusin.service;

import com.cpayusin.member.infrastructure.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cpayusin.dummy.DummyObject;
import com.cpayusin.common.security.jwt.JwtService;
import com.cpayusin.mapper.MemberMapper;
import com.cpayusin.member.controller.request.MemberRegisterRequest;
import com.cpayusin.member.controller.request.ResetPasswordDto;
import com.cpayusin.member.controller.request.VerificationDto;
import com.cpayusin.member.controller.response.MemberCreateResponse;
import com.cpayusin.member.controller.response.ResetPasswordResponse;
import com.cpayusin.common.service.RedisService;
import com.cpayusin.member.service.AuthenticationServiceImpl;
import com.cpayusin.member.service.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest extends DummyObject
{
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private MemberServiceImpl memberService;

    @Mock
    private RedisService redisService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Spy // 진짜 객체를 InjectMocks 에 주입
    private ObjectMapper om;


    @Test
    void memberCreateTest()
    {
        // given
        String nickname = "test";
        String email = "aaa@naver.com";
        String password = "12345";

        MemberRegisterRequest request = new MemberRegisterRequest();
        request.setNickname(nickname);
        request.setEmail(email);
        request.setPassword(password);

        Member member = newMockMember(1L, email, nickname, "ADMIN");

        // stub 1
        Member convertedMember = MemberMapper.INSTANCE.toMemberEntity(request);
        assertThat(convertedMember.getNickname()).isEqualTo(nickname);
        assertThat(convertedMember.getPassword()).isEqualTo(password);

        // stub 2
        String encodedPassword = passwordEncoder.encode(password);
        assertThat(passwordEncoder.matches(password, encodedPassword)).isTrue();

        // stub 3
        when(memberService.save(any(Member.class))).thenReturn(member);


        // when
        MemberCreateResponse response = authenticationService.register(request);

        System.out.println("response = " + response.toString());
        // then
        assertThat(response.getNickname()).isEqualTo(nickname);
        assertThat(response.getEmail()).isEqualTo(email);

    }

    @Test
    void verifyCode_test()
    {
        // given
        String email = "a.naver.com";
        String verificationCode = "ABCDEFGH";

        VerificationDto verificationDto = VerificationDto.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();

        // stub 1
        when(redisService.getVerificationCodeByEmail(any(String.class))).thenReturn(verificationCode);

        // when
        String response = authenticationService.verifyCode(verificationDto);

        // then
        assertThat(response).isEqualTo("인증이 완료되었습니다.");
    }

    @Test
    void resetPassword_test()
    {
        // given
        Member member = newMockMember(1L, "aa@naver.com", "test", "ADMIN");
        String newPassword = "12345";

        ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .email(member.getEmail())
                .password(newPassword)
                .build();

        // stub 1
        when(memberService.findMemberByEmail(any())).thenReturn(member);

        // stub 2
        member.updatePassword(passwordEncoder.encode(newPassword));

        // when
        ResetPasswordResponse response = authenticationService.resetPassword(resetPasswordDto);

        // then
        assertThat(response.getEmail()).isEqualTo("aa@naver.com");
        assertThat(passwordEncoder.matches("12345", member.getPassword())).isTrue();
    }

    @Test
    void logout_test()
    {
        // given
        String email = "aa@naver.com";
        String refreshToken = jwtService.generateRefreshToken(email);
        System.out.println("refresh token = " + refreshToken);


        // stub 1
        when(jwtService.isValidToken(any())).thenReturn(true);

        // stub 2
        when(redisService.hasKey(any())).thenReturn(true);
        doNothing().when(redisService).deleteRefreshToken(refreshToken);

        // when
        String response = authenticationService.logout(refreshToken);

        // then
        assertThat(response).isEqualTo("로그아웃에 성공했습니다");
        System.out.println("response = " + response);
    }

    @Test
    void setHeader_test()
    {
        // given
        String email = "aa@naver.com";
        List<String> roles = List.of("ADMIN");
        String accessToken = "ASDFWEFSFDSFWEFSDFSDFSDF";

        // when
        HttpHeaders httpHeaders = authenticationService.setHeadersWithNewAccessToken(accessToken);
        System.out.println("accessToken = " + accessToken);

        // then
        assertThat(httpHeaders.get(HttpHeaders.AUTHORIZATION).get(0)).isEqualTo("Bearer " + accessToken);
        System.out.println("authorization = " + httpHeaders.get("Authorization").get(0));
    }

}
