package com.jbaacount.service;

import com.jbaacount.payload.request.member.MemberRegisterRequest;
import com.jbaacount.payload.request.member.ResetPasswordDto;
import com.jbaacount.payload.request.member.VerificationDto;
import com.jbaacount.payload.response.AuthenticationResponse;
import com.jbaacount.payload.response.member.MemberCreateResponse;
import com.jbaacount.payload.response.member.ResetPasswordResponse;
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
