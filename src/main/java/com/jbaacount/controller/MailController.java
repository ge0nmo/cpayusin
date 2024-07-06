package com.jbaacount.controller;

import com.jbaacount.payload.request.member.SendVerificationCodeRequest;
import com.jbaacount.payload.response.GlobalResponse;
import com.jbaacount.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mail")
public class MailController
{
    private final MailService mailService;

    @PostMapping("/send-verification")
    public ResponseEntity<GlobalResponse<String>> sendVerificationCode(@Valid  @RequestBody SendVerificationCodeRequest request)
    {
        var data = mailService.sendVerificationCode(request);

        return ResponseEntity.ok(new GlobalResponse<>(data));
    }

}
