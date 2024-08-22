package com.cpayusin.member.service;

import com.cpayusin.common.service.RedisService;
import com.cpayusin.member.service.port.MailSendHelper;
import com.cpayusin.member.controller.port.MailService;
import com.cpayusin.member.controller.request.SendVerificationCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.cpayusin.common.service.UtilService.generateVerificationCode;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailServiceImpl implements MailService
{
    private final MailSendHelper helper;
    private final RedisService redisService;

    public String sendVerificationCode(SendVerificationCodeRequest request)
    {
        String email = request.getEmail();

        String verificationCode = generateVerificationCode();

        if(!redisService.isEmailLimited(email)){
            return "잠시 후 다시 시도해주세요.";
        }

        redisService.saveEmailAndVerificationCodeWith5Minutes(email, verificationCode);
        redisService.saveEmailForLimitationFor1Minute(email);

        helper.sendVerificationEmail(email, verificationCode);
        return "인증코드가 발송되었습니다. 5분 내로 인증을 완료해주세요.";
    }

}
