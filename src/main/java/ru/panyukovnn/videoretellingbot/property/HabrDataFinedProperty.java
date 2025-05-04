package ru.panyukovnn.videoretellingbot.property;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "retelling.data-finder.habr")
public class HabrDataFinedProperty {

    @Positive
    private Integer periodOfDaysToLookFor;

}
