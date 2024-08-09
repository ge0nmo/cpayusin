package com.jbaacount.service;

import com.jbaacount.payload.request.member.SendVerificationCodeRequest;

public interface MailService
{
    String sendVerificationCode(SendVerificationCodeRequest request);
}
