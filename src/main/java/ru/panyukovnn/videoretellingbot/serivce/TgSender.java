package ru.panyukovnn.videoretellingbot.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.panyukovnn.videoretellingbot.config.TgBotApi;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgSender {

    private final TgBotApi botApi;

    public void sendMessage(Long chatId, String message) {
        try {
            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .text(message)
                .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в телеграм: {}", e.getMessage(), e);
        }
    }
}
