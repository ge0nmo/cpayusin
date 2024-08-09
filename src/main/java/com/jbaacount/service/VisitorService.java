package com.jbaacount.service;

import com.jbaacount.model.Visitor;
import com.jbaacount.payload.response.VisitorResponse;
import com.jbaacount.repository.VisitorRepository;
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
public class VisitorService
{
    private final RedisTemplate<String, String> redisTemplate;
    private final VisitorRepository visitorRepository;
    private final static String VISITOR_KEY_PREFIX = "visitor:";

    public Visitor save(String ipAddress, LocalDate date)
    {
        Visitor visitor = Visitor.builder()
                .ipAddress(ipAddress)
                .date(date)
                .build();

        log.info("===visitor scheduler===");
        log.info("visitor info saved = {}", ipAddress);
        return visitorRepository.save(visitor);
    }


    @Scheduled(initialDelay = 1 * 60 * 1000, fixedDelay = 1 * 60 * 1000)
    public void updateVisitorData()
    {
        Set<String> keys = redisTemplate.keys(VISITOR_KEY_PREFIX + "*");

        for (String key : keys)
        {
            String[] parts = key.split(":");
            if(parts.length != 3){
                continue;
            }

            String ipAddress = parts[1];
            LocalDate date = LocalDate.parse(parts[2]);
            log.info("Visitor IP Address: {}", ipAddress);
            log.info("Visitor Date: {}", date);

            if(!visitorRepository.existsByIpAddressAndDate(ipAddress, date))
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
        Long count = visitorRepository.countByDate(date);

        return count != null ? count : 0;
    }

    private Long getYesterdayVisitors(LocalDate date)
    {
        Long count = visitorRepository.countByDate(date.minusDays(1));

        return count != null ? count : 0;
    }

    private Long getTotalVisitors()
    {
        Long count = visitorRepository.count();

        return count != null ? count : 0;
    }
}
