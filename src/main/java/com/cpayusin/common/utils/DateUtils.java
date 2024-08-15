package com.cpayusin.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateUtils
{
    public int getYearData()
    {
        return LocalDateTime.now().getYear();
    }

    public int getMonthData()
    {
        return LocalDateTime.now().getMonthValue();
    }
}
