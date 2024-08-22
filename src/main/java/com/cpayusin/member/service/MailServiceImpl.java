package com.cpayusin.member.service;

import com.cpayusin.common.service.EmailExecutorService;
import com.cpayusin.common.service.RedisService;
import com.cpayusin.member.controller.port.MailService;
import com.cpayusin.member.controller.request.SendVerificationCodeRequest;
import com.cpayusin.member.service.port.MailSendHelper;
import com.cpayusin.member.service.port.MailVerificationService;
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
    private final MailVerificationService mailVerificationService;
    private final RedisService redisService;
    private final EmailExecutorService emailExecutorService;

    public String sendVerificationCode(SendVerificationCodeRequest request)
    {
        String email = request.getEmail();
        mailVerificationService.verifyEmailLimitation(email);

        String verificationCode = generateVerificationCode();

        emailExecutorService.getExecutorService().submit(() ->{
            try{
                redisService.saveEmailAndVerificationCodeWith5Minutes(email, verificationCode);
                helper.sendVerificationEmail(email, verificationCode);
                redisService.saveEmailForLimitationFor1Minute(email);
            } catch (Exception e) {
                log.error("failed to send a verification code to email {}", email, e);
            }
        });

        return "인증코드가 발송되었습니다. 5분 내로 인증을 완료해주세요.";
    }
}
