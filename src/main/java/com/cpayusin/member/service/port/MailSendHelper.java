package com.cpayusin.member.service.port;

public interface MailSendHelper
{
    void sendVerificationEmail(String email, String verificationCode);
}
