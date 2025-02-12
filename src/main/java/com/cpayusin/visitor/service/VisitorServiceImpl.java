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

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService
{
    private final RedisTemplate<String, String> redisTemplate;
    private final VisitorRepository visitorRepository;
    private final DateUtils dateUtils;

    private final static String VISITOR_KEY_PREFIX = "visitor|";

    public Visitor save(String ipAddress, LocalDate date)
    {
        Visitor visitor = Visitor.builder()
                .ipAddress(ipAddress)
                .date(date)
                .build();

        return visitorRepository.save(visitor);
    }


    @Scheduled(initialDelay = 30 * 60 * 1000, fixedDelay = 30 * 60 * 1000)
    public void updateVisitorData()
    {
        // TODO => batch execution

        Set<String> keys = redisTemplate.keys(VISITOR_KEY_PREFIX + "*");
        for (String key : keys) {
            log.info("key = {}", key);

            String[] parts = key.split("\\|");
            if (parts.length != 3) {
                continue;
            }

            String ipAddress = parts[1];
            LocalDate date = LocalDate.parse(parts[2]);

            if (!visitorRepository.existsByIpAddressAndDate(ipAddress, date))
                save(ipAddress, date);

            log.info("visitor info terminated in redis");
            redisTemplate.delete(key);
        }
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
