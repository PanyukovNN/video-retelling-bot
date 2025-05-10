package ru.panyukovnn.videoretellingbot.serivce.eventprocessor.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.model.retelling.Retelling;
import ru.panyukovnn.videoretellingbot.property.PromptProperties;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.repository.RetellingRepository;
import ru.panyukovnn.videoretellingbot.serivce.eventprocessor.EventProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetellingEventProcessorImpl implements EventProcessor {

    private final OpenAiClient openAiClient;
    private final PromptProperties promptProperties;
    private final ContentRepository contentRepository;
    private final RetellingRepository retellingRepository;
    private final ProcessingEventRepository processingEventRepository;

    @Override
    public void process(ProcessingEvent processingEvent) {
        Content content = contentRepository.findById(processingEvent.getContentId())
            .orElse(null);

        if (content == null) {
            log.warn("Не удалось выполнить пересказ материала, поскольку не найден контент, событие будет удалено");
            processingEventRepository.delete(processingEvent);

            return;
        }

        String prompt = promptProperties.getRetelling();

        String retellingResponse = openAiClient.retellingBlockingCall("retelling", prompt, content.getContent());

        Retelling retelling = retellingRepository.save(Retelling.builder()
            .contentId(content.getId())
            .prompt(prompt)
            .aiModel("deepseek-chat")
            .retelling(retellingResponse)
            .tag("Java разработка")
            .build()
        );

        processingEvent.setType(ProcessingEventType.PUBLISH_RETELLING);
        processingEvent.setRetellingId(retelling.getId());
        processingEventRepository.save(processingEvent);

        log.info("Успешно выполнен пересказ материала по тегу: {}. Название материала: {}", retelling.getTag(), content.getTitle());
    }

    @Override
    public ProcessingEventType getProcessingEventType() {
        return ProcessingEventType.RETELLING;
    }

}
