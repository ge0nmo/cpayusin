package com.jbaacount.service;

import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.global.exception.InvalidTokenException;
import com.jbaacount.global.security.jwt.JwtService;
import com.jbaacount.mapper.MemberMapper;
import com.jbaacount.model.Member;
import com.jbaacount.model.type.Platform;
import com.jbaacount.model.type.Role;
import com.jbaacount.payload.request.member.MemberRegisterRequest;
import com.jbaacount.payload.request.member.ResetPasswordDto;
import com.jbaacount.payload.request.member.VerificationDto;
import com.jbaacount.payload.response.AuthenticationResponse;
import com.jbaacount.payload.response.member.MemberCreateResponse;
import com.jbaacount.payload.response.member.ResetPasswordResponse;
import com.jbaacount.repository.RedisRepository;
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
public class AuthenticationService
{
    private final MemberService memberService;
    private final RedisRepository redisRepository;
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
        String verificationCode = redisRepository.getVerificationCodeByEmail(verificationDto.getEmail());


        if(verificationCode == null)
            throw new BusinessLogicException(ExceptionMessage.EXPIRED_VERIFICATION_CODE);

        if(verificationCode.equals(verificationDto.getVerificationCode()))
        {
            redisRepository.deleteEmailAfterVerification(verificationDto.getEmail());
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

        if(!redisRepository.hasKey(refreshToken))
            throw new InvalidTokenException(ExceptionMessage.TOKEN_NOT_FOUND);

        redisRepository.deleteRefreshToken(refreshToken);
        SecurityContextHolder.clearContext();
        return "로그아웃에 성공했습니다";
    }

    public AuthenticationResponse reissue(String accessToken, String refreshToken)
    {
        if(redisRepository.hasKey(refreshToken))
        {
            log.info("===reissue===");
            log.info("accessToken = {}", accessToken);
            log.info("refreshToken = {}", refreshToken);

            Claims claims = jwtService.getClaims(accessToken.substring(7));
            String email = claims.getSubject();

            Member member = memberService.findMemberByEmail(email);

            String renewedAccessToken = jwtService.generateAccessToken(email);


            redisRepository.saveRefreshToken(refreshToken, email);

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
