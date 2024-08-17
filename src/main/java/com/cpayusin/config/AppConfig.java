package com.cpayusin.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class AppConfig
{
    @PostConstruct
    public void init()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

    }
}
