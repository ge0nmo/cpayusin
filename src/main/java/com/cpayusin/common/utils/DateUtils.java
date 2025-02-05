package com.cpayusin.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateUtils
{
    public LocalDate getToday()
    {
        return LocalDate.now();
    }

    public LocalDate getYesterday()
    {
        return LocalDate.now().minusDays(1);
    }
}
