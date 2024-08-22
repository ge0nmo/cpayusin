package com.cpayusin.member.repository;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.common.service.RedisService;
import com.cpayusin.member.service.port.MailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailVerificationServiceImpl implements MailVerificationService
{
    private final RedisService redisService;

    public void verifyEmailLimitation(String email)
    {
        if(redisService.isEmailLimited(email)){
            throw new BusinessLogicException(ExceptionMessage.MAIL_LIMITED);
        }
    }
}
