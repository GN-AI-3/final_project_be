package com.example.final_project_be.config;

import com.example.final_project_be.domain.trainer.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {

    private final TrainerService trainerService;
    
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredSubscriptions() {
        log.info("Running scheduled task to check for expired subscriptions");
        trainerService.checkAndUpdateExpiredSubscriptions();
    }
} 