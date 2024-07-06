package com.jbaacount.controller;

import com.jbaacount.payload.request.member.MemberRegisterRequest;
import com.jbaacount.payload.request.member.ResetPasswordDto;
import com.jbaacount.payload.request.member.VerificationDto;
import com.jbaacount.payload.response.AuthenticationResponse;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.payload.response.member.MemberCreateResponse;
import com.jbaacount.payload.response.member.ResetPasswordResponse;
import com.jbaacount.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class AuthenticationController
{
    private final AuthenticationService authenticationService;

    @PostMapping("/logout")
    public ResponseEntity<GlobalResponse<String>> logout(@RequestHeader(value = "Refresh") String refreshToken)
    {
        String data = authenticationService.logout(refreshToken);
        log.info("logout completed successfully");

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PostMapping("/reissue")
    public ResponseEntity<GlobalResponse<AuthenticationResponse>> reissue(@RequestHeader(value = "Authorization") String accessToken,
                                                                          @RequestHeader(value = "Refresh") String refreshToken)
    {
        AuthenticationResponse data = authenticationService.reissue(accessToken, refreshToken);

        authenticationService.setHeadersWithNewAccessToken(data.getAccessToken());

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<GlobalResponse<MemberCreateResponse>> signUp(@RequestBody @Valid MemberRegisterRequest request)
    {
        MemberCreateResponse data = authenticationService.register(request);

        log.info("===signUp===");
        log.info("user enrolled successfully");

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @GetMapping("/verification")
    public ResponseEntity<GlobalResponse<String>> verifyCode(@Valid @RequestBody VerificationDto verificationDto)
    {
        String data = authenticationService.verifyCode(verificationDto);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<GlobalResponse<ResetPasswordResponse>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto)
    {
        var data = authenticationService.resetPassword(resetPasswordDto);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }
}
