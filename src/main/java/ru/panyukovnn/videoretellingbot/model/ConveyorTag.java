package ru.panyukovnn.videoretellingbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConveyorTag {

    JAVA_HABR("java-habr"),
    TG_MESSAGE_BATCH("tg-message-batch");

    private final String propertyValue;
}
