package ru.panyukovnn.videoretellingbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumeContentRequest {

    /**
     * Для сообщений телеграм это будет номер телеграм чата
     */
    @Nullable
    @Schema(description = "Ссылка на контент")
    @NotEmpty
    private String link;

    @Schema(description = "Тип контента")
    @Pattern(regexp = "tg_message_batch")
    @NotEmpty
    private String contentType;

    @Schema(description = "Источник")
    @Pattern(regexp = "tg")
    @NotEmpty
    private String source;

    /**
     * Для сообщений телеграм тут будет заголовок чата + имя топика
     */
    @Schema(description = "Заголовок")
    @NotEmpty
    private String title;

    /**
     * Для пачки сообщений телеграм тут будет информация о временных метках первого и последнего сообщения, а также количество сообщений
     */
    @Schema(description = "Мета-информация")
    private String meta;

    @Schema(description = "Дата публикации")
    @NotNull
    private LocalDateTime publicationDate;

    @Schema(description = "Сам контент, формат может быть разным")
    @NotEmpty
    private String content;

    @Schema(description = "Тип конвейера, которым должен быть обработан контент")
    @Pattern(regexp = "just_retelling")
    @NotEmpty
    private String conveyorType;

    @Schema(description = "Сценарий конвейера")
    @Pattern(regexp = "java_habr|tg_message_batch")
    @NotEmpty
    private String tag;
}
