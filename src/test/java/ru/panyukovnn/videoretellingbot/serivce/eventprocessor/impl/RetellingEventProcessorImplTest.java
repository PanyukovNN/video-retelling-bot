package ru.panyukovnn.videoretellingbot.serivce.eventprocessor.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.model.retelling.Retelling;
import ru.panyukovnn.videoretellingbot.property.PromptProperties;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.repository.RetellingRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetellingEventProcessorImplTest {

    @Mock
    private OpenAiClient openAiClient;
    @Mock
    private PromptProperties promptProperties;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private RetellingRepository retellingRepository;
    @Mock
    private ProcessingEventRepository processingEventRepository;

    @InjectMocks
    private RetellingEventProcessorImpl retellingEventProcessor;

    @Test
    void when_process_withValidContent_then_success() {
        // Arrange
        UUID contentId = UUID.randomUUID();
        Content content = new Content();
        content.setId(contentId);
        content.setTitle("Test Title");
        content.setContent("Test Content");

        ProcessingEvent processingEvent = new ProcessingEvent();
        processingEvent.setContentId(contentId);

        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
        when(promptProperties.getRetelling()).thenReturn("Retelling prompt");
        when(openAiClient.retellingBlockingCall(anyString(), anyString(), anyString()))
            .thenReturn("Retelling content");
        when(retellingRepository.save(any(Retelling.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        retellingEventProcessor.process(processingEvent);

        // Assert
        verify(contentRepository).findById(contentId);
        verify(openAiClient).retellingBlockingCall("retelling", "Retelling prompt", "Test Content");
        verify(retellingRepository).save(argThat(retelling ->
            retelling.getContentId().equals(contentId) &&
            retelling.getPrompt().equals("Retelling prompt") &&
            retelling.getAiModel().equals("deepseek-chat") &&
            retelling.getRetelling().equals("Retelling content") &&
            retelling.getTag().equals("Java разработка")
        ));
        verify(processingEventRepository).save(argThat(event ->
            event.getType() == ProcessingEventType.PUBLISH_RETELLING
        ));
    }

    @Test
    void when_process_withNonExistentContent_then_deleteEvent() {
        // Arrange
        UUID contentId = UUID.randomUUID();
        ProcessingEvent processingEvent = new ProcessingEvent();
        processingEvent.setContentId(contentId);

        when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

        // Act
        retellingEventProcessor.process(processingEvent);

        // Assert
        verify(processingEventRepository).delete(processingEvent);
        verifyNoMoreInteractions(openAiClient, retellingRepository);
    }

    @Test
    void when_getProcessingEventType_then_returnRetelling() {
        // Act
        ProcessingEventType type = retellingEventProcessor.getProcessingEventType();

        // Assert
        assertEquals(ProcessingEventType.RETELLING, type);
    }
} 