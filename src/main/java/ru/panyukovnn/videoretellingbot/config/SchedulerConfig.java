package ru.panyukovnn.videoretellingbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public ScheduledExecutorService sourceParsingScheduler() {
        return new ScheduledThreadPoolExecutor(1, new ThreadPoolExecutor.DiscardPolicy());
    }

    @Bean
    public ScheduledExecutorService retellingScheduler() {
        return new ScheduledThreadPoolExecutor(1, new ThreadPoolExecutor.DiscardPolicy());
    }
}
