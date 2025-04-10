package ru.panyukovnn.videoretellingbot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.panyukovnn.videoretellingbot.config.TgBotApi;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.serivce.Summarizer;
import ru.panyukovnn.videoretellingbot.serivce.YoutubeSubtitlesExtractor;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotListener {

    private final TgBotApi botApi;
    private final Summarizer summarizer;
    private final YoutubeSubtitlesExtractor youtubeSubtitlesExtractor;

    @EventListener(Update.class)
    public void onUpdate(Update update) throws TelegramApiException {
        Long userId = Optional.ofNullable(update.getMessage())
                .map(Message::getFrom)
                .map(User::getId)
                .orElse(0L);
        String messageText = Optional.ofNullable(update.getMessage())
                .map(Message::getText)
                .orElse("Не удалось извлечь текст сообщения");

        log.info("Received message from user: {}. Text: {}", userId, messageText);

        Long chatId = Optional.ofNullable(update.getMessage())
            .map(Message::getChatId)
            .orElseThrow();

        try {
            // TODO check youtube link

            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .text("Начинаю извлечение субтитров")
                .build());

            String subtitles = youtubeSubtitlesExtractor.extractYoutubeVideoSubtitles(messageText);

            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Субтитры извлечены успешно. Отправляю запрос на пересказ содержания")
                .build());

            String videoSummary = summarizer.summarizeContent(subtitles);

            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text(videoSummary)
                .build());
        } catch (RetellingException e) {
            log.error("Ошибка бизнес логики. id: {}. Сообщение: {}", e.getId(), e.getMessage(), e);

            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text(e.getMessage())
                .build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);

            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text("Ошибка при отправке сообщения")
                .build());
        }
    }
}
