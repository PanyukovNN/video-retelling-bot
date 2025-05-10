package ru.panyukovnn.videoretellingbot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.serivce.eventprocessor.EventProcessor;

import java.util.List;
import java.util.Map;

import static ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessingJob {

    public static final List<ProcessingEventType> NON_TERMINAL_PROCESSING_EVENT_TYPES = List.of(
        RATE_RAW_MATERIAL,
        RETELLING,
        PUBLISH_RETELLING
    );

    private final ProcessingEventRepository processingEventRepository;
    private final Map<ProcessingEventType, EventProcessor> eventProcessorByType;

    @Async("publicationScheduler")
    @Scheduled(cron = "${retelling.scheduled-jobs.event-processing.cron}")
    public void processEvents() {
        List<ProcessingEvent> events = processingEventRepository.findAllByTypeIn(NON_TERMINAL_PROCESSING_EVENT_TYPES);

        events.forEach(event -> {
            try {
                eventProcessorByType.get(event.getType())
                    .process(event);
            } catch (Exception e) {
                log.error("Ошибка обработке события: {}. baseId: {}. Сообщение: {}", event.getType(), event.getBaseId(), e.getMessage(), e);
            }
        });

    }
}
