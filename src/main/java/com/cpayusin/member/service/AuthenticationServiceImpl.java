package com.cpayusin.member.service;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.common.exception.InvalidTokenException;
import com.cpayusin.common.security.jwt.JwtService;
import com.cpayusin.mapper.MemberMapper;
import com.cpayusin.member.controller.request.MemberRegisterRequest;
import com.cpayusin.member.controller.response.AuthenticationResponse;
import com.cpayusin.member.controller.response.MemberCreateResponse;
import com.cpayusin.member.controller.response.ResetPasswordResponse;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.member.domain.type.Platform;
import com.cpayusin.member.domain.type.Role;
import com.cpayusin.member.controller.request.ResetPasswordDto;
import com.cpayusin.member.controller.request.VerificationDto;
import com.cpayusin.common.service.RedisService;
import com.cpayusin.member.controller.port.AuthenticationService;
import com.cpayusin.member.controller.port.MemberService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthenticationServiceImpl implements AuthenticationService
{
    private final MemberService memberService;
    private final RedisService redisService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    public MemberCreateResponse register(MemberRegisterRequest request)
    {
        Member member = MemberMapper.INSTANCE.toMemberEntity(request);
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        member.setPlatform(Platform.HOME);
        member.setRole(Role.USER.getValue());
        Member savedMember = memberService.save(member);
        return MemberMapper.INSTANCE.toMemberCreateResponse(savedMember);

    }

    public String verifyCode(VerificationDto verificationDto)
    {
        String verificationCode = redisService.getVerificationCodeByEmail(verificationDto.getEmail());
        if (verificationCode == null)
            throw new BusinessLogicException(ExceptionMessage.EXPIRED_VERIFICATION_CODE);
        if (verificationCode.equals(verificationDto.getVerificationCode())) {
            redisService.deleteEmailAfterVerification(verificationDto.getEmail());
            log.info("email removed from redis successfully");
            return "인증이 완료되었습니다.";
        }
        throw new BusinessLogicException(ExceptionMessage.INVALID_VERIFICATION_CODE);
    }

    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordDto resetPasswordDto)
    {
        Member member = memberService.findMemberByEmail(resetPasswordDto.getEmail());
        member.updatePassword(passwordEncoder.encode(resetPasswordDto.getPassword().toString()));
        return MemberMapper.INSTANCE.toResetPasswordResponse(member);
    }


    public String logout(String refreshToken)
    {
        jwtService.isValidToken(refreshToken);
        if (!redisService.hasKey(refreshToken))
            throw new InvalidTokenException(ExceptionMessage.TOKEN_NOT_FOUND);
        redisService.deleteRefreshToken(refreshToken);
        SecurityContextHolder.clearContext();
        return "로그아웃에 성공했습니다";
    }

    public AuthenticationResponse reissue(String accessToken, String refreshToken)
    {
        if (redisService.hasKey(refreshToken)) {
            log.info("===reissue===");
            log.info("accessToken = {}", accessToken);
            log.info("refreshToken = {}", refreshToken);
            Claims claims = jwtService.getClaims(accessToken.substring(7));
            String email = claims.getSubject();
            Member member = memberService.findMemberByEmail(email);
            String renewedAccessToken = jwtService.generateAccessToken(email);
            redisService.saveRefreshToken(refreshToken, email);
            return AuthenticationResponse.builder()
                    .memberId(member.getId())
                    .email(email)
                    .role(member.getRole())
                    .accessToken(renewedAccessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new InvalidTokenException(ExceptionMessage.TOKEN_NOT_FOUND);
    }

    public HttpHeaders setHeadersWithNewAccessToken(String newAccessToken)
    {
        HttpHeaders response = new HttpHeaders();
        response.set(AUTHORIZATION, "Bearer " + newAccessToken);
        return response;
    }
}
