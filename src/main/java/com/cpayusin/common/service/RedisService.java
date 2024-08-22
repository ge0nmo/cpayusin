package com.cpayusin.common.service;

public interface RedisService
{
    void saveRefreshToken(String refreshToken, String email);
    void deleteEmailAfterVerification(String email);
    void saveEmailAndVerificationCodeWith5Minutes(String email, String verificationCode);

    void saveEmailForLimitationFor1Minute(String email);
    boolean isEmailLimited(String email);
    String getVerificationCodeByEmail(String email);
    void deleteRefreshToken(String refreshToken);
    Boolean hasKey(String refreshToken);
}
