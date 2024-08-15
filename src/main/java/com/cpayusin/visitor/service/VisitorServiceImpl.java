package com.cpayusin.visitor.service;

import com.cpayusin.visitor.infrastructure.VisitorEntity;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService
{
    private final RedisTemplate<String, String> redisTemplate;
    private final VisitorRepository visitorRepository;
    private final static String VISITOR_KEY_PREFIX = "visitor:";

    public VisitorEntity save(String ipAddress, LocalDate date)
    {
        VisitorEntity visitor = VisitorEntity.builder()
                .ipAddress(ipAddress)
                .date(date)
                .build();
        log.info("===visitor scheduler===");
        log.info("visitor info saved = {}", ipAddress);
        return visitorRepository.save(visitor);
    }


    @Scheduled(initialDelay = 30 * 60 * 1000, fixedDelay = 30 * 60 * 1000)
    public void updateVisitorData()
    {
        // TODO => batch execution

        Set<String> keys = redisTemplate.keys(VISITOR_KEY_PREFIX + "*");
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length != 3) {
                continue;
            }
            String ipAddress = parts[1];
            LocalDate date = LocalDate.parse(parts[2]);
            log.info("Visitor IP Address: {}", ipAddress);
            log.info("Visitor Date: {}", date);
            if (!visitorRepository.existsByIpAddressAndDate(ipAddress, date))
                save(ipAddress, date);
            log.info("visitor info terminated in redis");
            redisTemplate.delete(key);
        }
    }

    public VisitorResponse getVisitorResponse()
    {
        LocalDate today = LocalDate.now();
        return VisitorResponse.builder()
                .yesterday(getYesterdayVisitors(today))
                .today(getTodayVisitors(today))
                .total(getTotalVisitors())
                .build();
    }

    private Long getTodayVisitors(LocalDate date)
    {
        return visitorRepository.countByDate(date);
    }

    private Long getYesterdayVisitors(LocalDate date)
    {
        return visitorRepository.countByDate(date.minusDays(1));
    }

    private Long getTotalVisitors()
    {
        return visitorRepository.count();
    }
}
