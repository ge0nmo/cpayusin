package com.cpayusin.member.controller.port;

import com.cpayusin.member.controller.request.MemberRegisterRequest;
import com.cpayusin.member.controller.response.AuthenticationResponse;
import com.cpayusin.member.controller.response.MemberCreateResponse;
import com.cpayusin.member.controller.response.ResetPasswordResponse;
import com.cpayusin.member.controller.request.ResetPasswordDto;
import com.cpayusin.member.controller.request.VerificationDto;
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
