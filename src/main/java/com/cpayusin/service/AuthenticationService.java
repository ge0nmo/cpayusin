package com.cpayusin.service;

import com.cpayusin.payload.request.member.MemberRegisterRequest;
import com.cpayusin.payload.request.member.ResetPasswordDto;
import com.cpayusin.payload.request.member.VerificationDto;
import com.cpayusin.payload.response.AuthenticationResponse;
import com.cpayusin.payload.response.member.MemberCreateResponse;
import com.cpayusin.payload.response.member.ResetPasswordResponse;
import org.springframework.http.HttpHeaders;

public interface AuthenticationService
{
    MemberCreateResponse register(MemberRegisterRequest request);

    String verifyCode(VerificationDto verificationDto);

    ResetPasswordResponse resetPassword(ResetPasswordDto resetPasswordDto);

    String logout(String refreshToken);

    AuthenticationResponse reissue(String accessToken, String refreshToken);

    HttpHeaders setHeadersWithNewAccessToken(String newAccessToken);
}
