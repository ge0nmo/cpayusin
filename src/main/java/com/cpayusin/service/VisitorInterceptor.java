package com.cpayusin.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class VisitorInterceptor implements HandlerInterceptor
{
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler)
    {
        String ipAddress = getClientIp(request);
        LocalDate date = LocalDate.now();
        String key = "visitor:" + ipAddress + ":" + date;

        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, "", 1, TimeUnit.DAYS);
            log.info("ip = {}, date = {}",ipAddress, date);
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request)
    {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(StringUtils.hasLength(ipAddress)){
            ipAddress = ipAddress.split(",")[0];
        } else{
            ipAddress = request.getHeader("X-Real-IP");
            if(!StringUtils.hasLength(ipAddress)){
                ipAddress = request.getRemoteAddr();
            }
        }

        return ipAddress;
    }
}