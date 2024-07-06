package com.jbaacount.repository;

import com.jbaacount.global.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisRepository
{
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public void saveRefreshToken(String refreshToken, String email)
    {
        int refreshTokenExpirationMinutes = jwtService.getRefreshTokenExpirationMinutes();

        redisTemplate.opsForValue().set(refreshToken, email, Duration.ofMinutes(refreshTokenExpirationMinutes));
    }

    public void deleteEmailAfterVerification(String email)
    {
        redisTemplate.delete(email);
    }

    public void saveEmailAndVerificationCodeWith5Minutes(String email, String verificationCode)
    {
        redisTemplate.opsForValue().set(email, verificationCode, Duration.ofMinutes(5));
    }

    public String getVerificationCodeByEmail(String email)
    {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteRefreshToken(String refreshToken)
    {
        redisTemplate.delete(refreshToken);
    }

    public Boolean hasKey(String refreshToken)
    {
        try{
            jwtService.isValidToken(refreshToken);
        }catch (RuntimeException ex){
            log.error(ex.getMessage(), ex);
            return false;
        }

        return redisTemplate.hasKey(refreshToken);
    }
}
