package com.cpayusin.visitor.controller.port;

import com.cpayusin.common.utils.CommonFunction;
import com.cpayusin.common.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static com.cpayusin.common.utils.CommonProperties.VISITOR_KEY_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Component
public class VisitorInterceptor implements HandlerInterceptor
{
    private final RedisTemplate<String, String> redisTemplate;
    private final DateUtils dateUtils;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler)
    {
        String ipAddress = CommonFunction.getIpAddress(request);
        LocalDate date = dateUtils.getToday();
        String key = VISITOR_KEY_PREFIX + ipAddress + "|" + date.toString();

        if(!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, "", 1, TimeUnit.DAYS);
            log.info("ip = {}, date = {}",ipAddress, date);
        }

        return true;
    }

}