package com.cpayusin.member.controller.port;

import com.cpayusin.member.controller.request.SendVerificationCodeRequest;

public interface MailService
{
    String sendVerificationCode(SendVerificationCodeRequest request);
}
