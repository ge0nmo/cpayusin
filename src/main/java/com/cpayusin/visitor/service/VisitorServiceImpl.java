package com.cpayusin.visitor.service;

import com.cpayusin.common.utils.DateUtils;
import com.cpayusin.visitor.domain.Visitor;
import com.cpayusin.visitor.controller.response.VisitorResponse;
import com.cpayusin.visitor.controller.port.VisitorService;
import com.cpayusin.visitor.service.port.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

import static com.cpayusin.common.utils.CommonProperties.VISITOR_KEY_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService
{
    private final RedisTemplate<String, String> redisTemplate;
    private final VisitorRepository visitorRepository;
    private final DateUtils dateUtils;

    public Visitor save(String ipAddress, LocalDate date)
    {
        Visitor visitor = Visitor.from(ipAddress, date);

        return visitorRepository.save(visitor);
    }


    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    public void updateVisitorData()
    {
        // TODO => batch execution

        Set<String> keys = redisTemplate.keys(VISITOR_KEY_PREFIX + "*");
        for (String key : keys) {
            String[] parts = fetchAndProcessVisitorKey(key);

            if(parts == null) continue;

            String ipAddress = parts[0];
            LocalDate date = LocalDate.parse(parts[1]);

            if (!visitorRepository.existsByIpAddressAndDate(ipAddress, date))
                save(ipAddress, date);

            log.info("visitor info terminated in redis");
            redisTemplate.delete(key);
        }
    }

    private String[] fetchAndProcessVisitorKey(String key)
    {
        log.info("key = {}", key);

        if(!key.startsWith(VISITOR_KEY_PREFIX)) return null;

        key = key.replace(VISITOR_KEY_PREFIX, "");
        return key.split("\\|");
    }

    public VisitorResponse getVisitorResponse()
    {
        return VisitorResponse.builder()
                .yesterday(getYesterdayVisitors())
                .today(getTodayVisitors())
                .total(getTotalVisitors())
                .build();
    }

    private Long getTodayVisitors()
    {
        LocalDate date = dateUtils.getToday();
        return visitorRepository.countByDate(date);
    }

    private Long getYesterdayVisitors()
    {
        LocalDate date = dateUtils.getYesterday();
        return visitorRepository.countByDate(date);
    }

    private Long getTotalVisitors()
    {
        return visitorRepository.count();
    }
}
