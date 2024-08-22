package com.cpayusin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class ServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ServerApplication.class, args);
    }

}
