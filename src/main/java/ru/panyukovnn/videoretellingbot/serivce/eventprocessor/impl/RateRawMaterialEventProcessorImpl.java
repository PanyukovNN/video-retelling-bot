package ru.panyukovnn.videoretellingbot.serivce.eventprocessor.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.exception.RawMaterialRateException;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.ContentRate;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.property.PromptProperties;
import ru.panyukovnn.videoretellingbot.repository.ContentRateRepository;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.serivce.eventprocessor.EventProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateRawMaterialEventProcessorImpl implements EventProcessor {

    private final OpenAiClient openAiClient;
    private final PromptProperties promptProperties;
    private final ContentRepository contentRepository;
    private final ContentRateRepository contentRateRepository;
    private final ProcessingEventRepository processingEventRepository;

    @Override
    public void process(ProcessingEvent processingEvent) {
        Content content = contentRepository.findById(processingEvent.getContentId())
            .orElse(null);

        if (content == null) {
            log.warn("Не удалось выполнить оценку сырого материала, поскольку не найден контент, событие будет удалено");
            processingEventRepository.delete(processingEvent);

            return;
        }

        String prompt = promptProperties.getRateMaterial();

        String retellingResponse = openAiClient.retellingBlockingCall("rate_material", prompt, content.getContent());

        String rawRate = "";
        for (int i = 0; i < 7; i++) {
            char c = retellingResponse.charAt(i);
            if (Character.isDigit(c)) {
                rawRate += c;
            }
        }
        boolean parsable = NumberUtils.isParsable(rawRate);
        if (!parsable) {
            throw new RawMaterialRateException("2c0b", "Не удалось распарсить оценку материала: " + rawRate + ". Ответ от ИИ: " + retellingResponse);
        }

        ContentRate contentRate = contentRateRepository.save(ContentRate.builder()
            .contentId(content.getId())
            .rate(Integer.parseInt(rawRate))
            .prompt(prompt)
            .grounding(retellingResponse)
            .tag("Java разработка")
            .build()
        );

        processingEvent.setType(ProcessingEventType.RETELLING);
        processingEventRepository.save(processingEvent);

        log.info("Успешно выполнена оценка материала по тегу: {}. Название материала: {}", contentRate.getTag(), content.getTitle());
    }

    @Override
    public ProcessingEventType getProcessingEventType() {
        return ProcessingEventType.RATE_RAW_MATERIAL;
    }

}
