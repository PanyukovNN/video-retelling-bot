package ru.panyukovnn.videoretellingbot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.serivce.RetellingHandler;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotListener {

    private final TgSender tgSender;
    private final RetellingHandler retellingHandler;

    @EventListener(Update.class)
    @Async("tgListenerExecutor")
    public void onUpdate(Update update) {
        Long userId = Optional.ofNullable(update.getMessage())
            .map(Message::getFrom)
            .map(User::getId)
            .orElse(0L);
        String messageText = Optional.ofNullable(update.getMessage())
            .map(Message::getText)
            .map(String::trim)
            .orElse("Не удалось извлечь текст сообщения");

        log.info("Received message from user: {}. Text: {}", userId, messageText);

        Long chatId = Optional.ofNullable(update.getMessage())
            .map(Message::getChatId)
            .orElseThrow();

        try {
            retellingHandler.handleRetelling(chatId, messageText);
        } catch (RetellingException e) {
            log.error("Ошибка бизнес логики. id: {}. Сообщение: {}", e.getId(), e.getMessage(), e);

            tgSender.sendMessage(chatId, "В процессе работы возникла ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            tgSender.sendMessage(chatId, "Непредвиденная ошибка при отправке сообщения");
        }
    }

}