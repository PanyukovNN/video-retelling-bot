package ru.panyukovnn.videoretellingbot.serivce.eventprocessor.impl;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.exception.RawMaterialRateException;
import ru.panyukovnn.videoretellingbot.model.ConveyorTag;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.ContentRate;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.property.ConveyorTagProperties;
import ru.panyukovnn.videoretellingbot.property.PublishingProperties;
import ru.panyukovnn.videoretellingbot.property.RateProperties;
import ru.panyukovnn.videoretellingbot.repository.ContentRateRepository;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.serivce.domain.ProcessingEventDomainService;
import ru.panyukovnn.videoretellingbot.serivce.eventprocessor.EventProcessor;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateRawMaterialEventProcessorImpl implements EventProcessor {

    private final TgSender tgSender;
    private final OpenAiClient openAiClient;
    private final RateProperties rateProperties;
    private final ContentRepository contentRepository;
    private final PublishingProperties publishingProperties;
    private final ConveyorTagProperties conveyorTagProperties;
    private final ContentRateRepository contentRateRepository;
    private final ProcessingEventDomainService processingEventDomainService;

    @Override
    public void process(ProcessingEvent processingEvent) {
        Content content = contentRepository.findById(processingEvent.getContentId())
            .orElse(null);

        if (content == null) {
            log.warn("Не удалось выполнить оценку сырого материала, поскольку не найден контент, событие будет удалено");
            processingEventDomainService.delete(processingEvent);

            return;
        }

        ConveyorTag tag = processingEvent.getConveyorTag();
        ConveyorTagProperties.ConveyorTagConfig conveyorTagConfig = conveyorTagProperties.getWithGuarantee(tag);
        String prompt = conveyorTagConfig.getRateMaterialPrompt();

        if (prompt == null) {
            log.warn("Не удалось определить rateMaterial prompt по тегу: {}", tag);

            return;
        }

        String retellingResponse = openAiClient.retellingBlockingCall("rate_material", prompt, content.getContent());

        StringBuilder rawRate = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            char c = retellingResponse.charAt(i);
            if (Character.isDigit(c)) {
                rawRate.append(c);
            }
        }
        boolean parsable = NumberUtils.isParsable(rawRate.toString());
        if (!parsable) {
            throw new RawMaterialRateException("2c0b", "Не удалось распарсить оценку материала: " + rawRate + ". Ответ от ИИ: " + retellingResponse);
        }

        int rate = Integer.parseInt(rawRate.toString());

        ContentRate contentRate = contentRateRepository.save(ContentRate.builder()
            .contentId(content.getId())
            .rate(rate)
            .prompt(prompt)
            .grounding(retellingResponse)
            .tag("Java разработка")
            .build()
        );

        if (rate < rateProperties.getThreshold()) {
            processingEvent.setType(ProcessingEventType.UNDERRATED);

            log.info("Материал имеет слишком низкую оценку и не будет пересказан: {}. rate: {}. Название материала: {}", contentRate.getTag(), rate, content.getTitle());
        } else {
            processingEvent.setType(ProcessingEventType.RETELLING);

            log.info("Успешно выполнена оценка материала по тегу: {}. Название материала: {}", contentRate.getTag(), content.getTitle());
        }

        processingEventDomainService.save(processingEvent);

        String formattedMessage = formatMessage(content, contentRate.getGrounding());

        tgSender.sendMessage(publishingProperties.getChatId(), publishingProperties.getRateTgTopicId(), formattedMessage);
    }

    @Override
    public ProcessingEventType getProcessingEventType() {
        return ProcessingEventType.RATE_RAW_MATERIAL;
    }

    private static String formatMessage(@Nullable Content content, String retelling) {
        if (content == null || content.getLink() == null) {
            return retelling;
        }

        String title = Optional.of(content)
            .map(Content::getTitle)
            .filter(StringUtils::hasText)
            .orElse("Ссылка");

        String firstLine = "[" + title + "](" + content.getLink() + ")\n\n";

        return firstLine + retelling;
    }
}
