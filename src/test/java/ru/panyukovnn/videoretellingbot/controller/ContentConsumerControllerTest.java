package ru.panyukovnn.videoretellingbot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.videoretellingbot.AbstractTest;
import ru.panyukovnn.videoretellingbot.dto.ConsumeContentRequest;
import ru.panyukovnn.videoretellingbot.dto.common.CommonRequest;
import ru.panyukovnn.videoretellingbot.model.ConveyorType;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.ContentType;
import ru.panyukovnn.videoretellingbot.model.content.Source;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentConsumerControllerTest extends AbstractTest {

    @Test
    @Transactional
    void when_consume_withValidRequest_then_success() throws Exception {
        // Arrange
        ConsumeContentRequest consumeContentRequest = new ConsumeContentRequest();
        consumeContentRequest.setLink("https://test.com");
        consumeContentRequest.setContentType("tg_message_batch");
        consumeContentRequest.setSource("tg");
        consumeContentRequest.setTitle("Test Title");
        consumeContentRequest.setMeta("{\"test\":\"meta\"}");
        consumeContentRequest.setPublicationDate(LocalDateTime.now());
        consumeContentRequest.setContent("Test Content");
        consumeContentRequest.setConveyorType("just_retelling");
        consumeContentRequest.setTag("tg_message_batch");

        CommonRequest<ConsumeContentRequest> request = new CommonRequest<>();
        request.setBody(consumeContentRequest);

        // Act
        mockMvc.perform(post("/api/v1/content/consume")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // Assert
        Content savedContent = contentRepository.findByLink("https://test.com")
            .orElse(null);
        assertNotNull(savedContent);
        assertEquals("Test Title", savedContent.getTitle());
        assertEquals("{\"test\":\"meta\"}", savedContent.getMeta());
        assertEquals("Test Content", savedContent.getContent());
        assertEquals(ContentType.TG_MESSAGE_BATCH, savedContent.getType());
        assertEquals(Source.TG, savedContent.getSource());

        ProcessingEvent processingEvent = processingEventRepository.findAll().stream()
            .filter(event -> event.getContentId().equals(savedContent.getId()))
            .findFirst()
            .orElse(null);
        assertNotNull(processingEvent);
        assertEquals(ProcessingEventType.RETELLING, processingEvent.getType());
        assertEquals(ConveyorType.JUST_RETELLING, processingEvent.getConveyorType());
    }

    @Test
    @Transactional
    void when_consume_withInvalidConveyorType_then_badRequest() throws Exception {
        // Arrange
        ConsumeContentRequest consumeContentRequest = new ConsumeContentRequest();
        consumeContentRequest.setLink("https://test.com");
        consumeContentRequest.setContentType("tg_message_batch");
        consumeContentRequest.setSource("tg");
        consumeContentRequest.setTitle("Test Title");
        consumeContentRequest.setMeta("Test Meta");
        consumeContentRequest.setPublicationDate(LocalDateTime.now());
        consumeContentRequest.setContent("Test Content");
        consumeContentRequest.setConveyorType("rating_and_retelling"); // Неподдерживаемый тип конвейера
        consumeContentRequest.setTag("tg_message_batch");

        CommonRequest<ConsumeContentRequest> request = new CommonRequest<>();
        request.setBody(consumeContentRequest);

        // Act & Assert
        mockMvc.perform(post("/api/v1/content/consume")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertTrue(contentRepository.findByLink("https://test.com").isEmpty());
        assertTrue(processingEventRepository.findAll().isEmpty());
    }

    @Test
    @Transactional
    void when_consume_withInvalidRequest_then_badRequest() throws Exception {
        // Arrange
        ConsumeContentRequest consumeContentRequest = new ConsumeContentRequest();
        // Не заполняем обязательные поля

        CommonRequest<ConsumeContentRequest> request = new CommonRequest<>();
        request.setBody(consumeContentRequest);

        // Act & Assert
        mockMvc.perform(post("/api/v1/content/consume")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertTrue(contentRepository.findAll().isEmpty());
        assertTrue(processingEventRepository.findAll().isEmpty());
    }
} 