package com.jbaacount.service;

import com.jbaacount.payload.response.VisitorResponse;
import com.jbaacount.model.Visitor;
import com.jbaacount.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService
{
    private final RedisTemplate<String, String> redisTemplate;
    private final VisitorRepository visitorRepository;

    @Scheduled(initialDelay = 1800000, fixedDelay = 1800000)
    public void updateVisitorData()
    {
        Set<String> keys = redisTemplate.keys("*_*");

        for (String key : keys)
        {
            log.info("key = {}", key);
            String[] parts = key.split("_");
            String ipAddress = parts[0];
            LocalDate date = null;

            if(isValideDate(parts[1]))
            {
                date = LocalDate.parse(parts[1]);
            }
            else
                continue;

            log.info("===visitor service & updateVisitorData()===");
            log.info("ipAddress = {}", ipAddress);
            log.info("date = {}", date);

            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            String userAgent = valueOperations.get(key);

            if(!visitorRepository.existsByIpAddressAndDate(ipAddress, date))
            {
                Visitor visitor = Visitor.builder()
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .date(date)
                        .build();

                log.info("===visitor scheduler===");
                log.info("visitor info saved = {}", ipAddress);
                visitorRepository.save(visitor);
            }

            log.info("visitor info terminated in redis");
            redisTemplate.delete(key);
        }
    }

    public VisitorResponse getVisitorResponse()
    {
        LocalDate today = LocalDate.now();

        VisitorResponse response = VisitorResponse.builder()
                .yesterday(getYesterdayVisitors(today))
                .today(getTodayVisitors(today))
                .total(getTotalVisitors())
                .build();

        return response;
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

    private boolean isValideDate(String dateString)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try{
            formatter.parse(dateString);
            return true;
        } catch (DateTimeParseException e){
            return false;
        }
    }
}
