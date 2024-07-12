package com.example.multiple_thread.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(2); // default thread in pool size is 2
        threadPoolExecutor.setCorePoolSize(5); // if Thread in pool size busy then create a new thread, and maximum is 5 , then busy then wait
        threadPoolExecutor.setCorePoolSize(100); // minimum thread waiting is 100
        threadPoolExecutor.setThreadNamePrefix("user-Thread-");
        threadPoolExecutor.initialize();
        return threadPoolExecutor;
    }
}
