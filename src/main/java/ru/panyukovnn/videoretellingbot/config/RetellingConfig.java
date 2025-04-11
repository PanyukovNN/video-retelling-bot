package ru.panyukovnn.videoretellingbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.panyukovnn.videoretellingbot.property.ExecutorsProperty;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Configuration
public class RetellingConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService tgListenerExecutor(ExecutorsProperty executorsProperty) {
        return new ThreadPoolExecutor(
            executorsProperty.getTgListener().getThreads(),
            executorsProperty.getTgListener().getThreads(),
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(executorsProperty.getTgListener().getQueueCapacity()),
            new ThreadPoolExecutor.AbortPolicy());
    }
}
