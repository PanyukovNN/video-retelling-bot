package ru.panyukovnn.videoretellingbot.serivce.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.panyukovnn.videoretellingbot.config.TgBotApi;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgSender {

    private final TgBotApi botApi;
    private final TgMessagePreparer tgMessagePreparer;

    public void sendMessage(Long chatId, String message) {
        List<String> splitLongMessages = tgMessagePreparer.prepareLongTgMessage(message);

        splitLongMessages.forEach(splitMessage -> executeSendMessage(chatId, splitMessage));
    }

    private void executeSendMessage(Long chatId, String message) {
        try {
            botApi.execute(SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text(message)
                .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в телеграм: {}", e.getMessage(), e);
        }
    }
}
