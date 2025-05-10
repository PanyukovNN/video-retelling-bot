package ru.panyukovnn.videoretellingbot.serivce.eventprocessor;

import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;

public interface EventProcessor {

    void process(ProcessingEvent processingEvent);

    ProcessingEventType getProcessingEventType();
}
