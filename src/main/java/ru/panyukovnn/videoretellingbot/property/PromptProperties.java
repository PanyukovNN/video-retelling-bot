package ru.panyukovnn.videoretellingbot.property;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "retelling.prompt")
public class PromptProperties {

    private String retelling;
    private String rateMaterial;

    @PostConstruct
    public void pc() {
        System.out.println(this.retelling);
        System.out.println(this.rateMaterial);
    }
}
