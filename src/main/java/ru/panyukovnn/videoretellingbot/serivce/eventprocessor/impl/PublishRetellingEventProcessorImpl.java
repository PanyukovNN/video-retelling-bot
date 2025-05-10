package ru.panyukovnn.videoretellingbot.serivce.eventprocessor.impl;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.model.retelling.Retelling;
import ru.panyukovnn.videoretellingbot.property.PublishingProperties;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.repository.RetellingRepository;
import ru.panyukovnn.videoretellingbot.serivce.eventprocessor.EventProcessor;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishRetellingEventProcessorImpl implements EventProcessor {

    private final TgSender tgSender;
    private final ContentRepository contentRepository;
    private final RetellingRepository retellingRepository;
    private final PublishingProperties publishingProperties;
    private final ProcessingEventRepository processingEventRepository;

    @Override
    public void process(ProcessingEvent processingEvent) {
        Retelling retelling = retellingRepository.findById(processingEvent.getBaseId())
            .orElse(null);

        if (retelling == null) {
            log.warn("Не удалось выполнить публикацию пересказа, поскольку не найден пересказ, событие будет удалено");
            processingEventRepository.delete(processingEvent);

            return;
        }

        try {
            Content content = contentRepository.findById(retelling.getContentId())
                .orElse(null);

            String formattedMessage = formatMessage(content, retelling.getRetelling());

            tgSender.sendMessage(publishingProperties.getChatId(), publishingProperties.getThreadId(), formattedMessage);

            log.info("Успешно выполнена отправка пересказа материала по тегу: {}. Название материала: {}", retelling.getTag(), content != null ? content.getTitle() : "undefined");

            processingEvent.setType(ProcessingEventType.PUBLISHED);
        } catch (Exception e) {
            log.error("Ошибка при отправке пересказа в телеграм: {}", e.getMessage(), e);

            processingEvent.setType(ProcessingEventType.PUBLICATION_ERROR);
        }

        processingEventRepository.save(processingEvent);
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

    @Override
    public ProcessingEventType getProcessingEventType() {
        return ProcessingEventType.PUBLISH_RETELLING;
    }

}
