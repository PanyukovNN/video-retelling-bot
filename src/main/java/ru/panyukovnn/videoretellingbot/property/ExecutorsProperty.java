package ru.panyukovnn.videoretellingbot.property;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "retelling.executors")
public class ExecutorsProperty {

    private ExecutorProperty tgListener;

    @Data
    public static class ExecutorProperty {

        private Integer threads;
        private Integer queueCapacity;
    }
}
