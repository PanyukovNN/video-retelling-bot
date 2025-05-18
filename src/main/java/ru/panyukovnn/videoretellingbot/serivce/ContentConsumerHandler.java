package ru.panyukovnn.videoretellingbot.serivce;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.videoretellingbot.dto.ConsumeContentRequest;
import ru.panyukovnn.videoretellingbot.model.ConveyorTag;
import ru.panyukovnn.videoretellingbot.model.ConveyorType;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.ContentType;
import ru.panyukovnn.videoretellingbot.model.content.Source;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.serivce.domain.ContentDomainService;
import ru.panyukovnn.videoretellingbot.serivce.domain.ProcessingEventDomainService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentConsumerHandler {

    private final ContentDomainService contentDomainService;
    private final ProcessingEventDomainService processingEventDomainService;

    @Transactional
    public void handleConsumeContent(ConsumeContentRequest consumeContentRequest) {
        if (ConveyorType.valueOf(consumeContentRequest.getConveyorType().toUpperCase()) != ConveyorType.JUST_RETELLING) {
            throw new ValidationException("Задан неподдерживаемый тип конвейера");
        }

        Content content = Content.builder()
            .link(consumeContentRequest.getLink())
            .type(ContentType.valueOf(consumeContentRequest.getContentType().toUpperCase()))
            .source(Source.valueOf(consumeContentRequest.getSource().toUpperCase()))
            .title(consumeContentRequest.getTitle())
            .meta(consumeContentRequest.getMeta())
            .publicationDate(consumeContentRequest.getPublicationDate())
            .content(consumeContentRequest.getContent())
            .build();

        contentDomainService.save(content);

        ConveyorType conveyorType = ConveyorType.valueOf(consumeContentRequest.getConveyorType().toUpperCase());
        ConveyorTag conveyorTag = ConveyorTag.valueOf(consumeContentRequest.getTag().toUpperCase());

        ProcessingEvent processingEvent = ProcessingEvent.builder()
            .contentId(content.getId())
            .type(conveyorType.getStartEventType())
            .conveyorTag(conveyorTag)
            .build();
        processingEventDomainService.save(processingEvent);
    }
}
