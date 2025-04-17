package com.example.final_project_be.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);     // 동시에 실행 가능한 쓰레드 최소 수
        executor.setMaxPoolSize(50);     // 동시에 실행 가능한 쓰레드 최대 수
        executor.setQueueCapacity(1000); // 큐에 쌓이는 작업 수
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Uncaught async error in method: " + method.getName() + ", message=" + ex.getMessage(), ex);
        };
    }
}
