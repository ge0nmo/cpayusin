package com.cpayusin.service;

import com.cpayusin.payload.request.member.SendVerificationCodeRequest;

public interface MailService
{
    String sendVerificationCode(SendVerificationCodeRequest request);
}
