package com.cpayusin.common.service;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Component
public class EmailExecutorService
{
    private final ExecutorService executorService;

    public EmailExecutorService()
    {
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @PreDestroy
    private void shutdownAndAwaitTermination()
    {
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(10, TimeUnit.SECONDS)){
                log.error("Service shutdown failed -> forced termination");

                executorService.shutdownNow();
                if(!executorService.awaitTermination(10, TimeUnit.SECONDS)){
                    log.error("Service has not been terminated");
                }
            }
        } catch (InterruptedException e){
            log.error("shutdown interrupted, forcing shutdown...");
            executorService.shutdownNow();
        }
    }
}
