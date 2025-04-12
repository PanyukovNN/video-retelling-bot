package ru.panyukovnn.videoretellingbot.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Service
public class StartCommand extends BotCommand {

    public static final String GREETING_MESSAGE = "Привет, я могу подготовить развернутый конспект по видео с youtube, пришли мне ссылку на видео, которое хочешь законспектировать";

    public StartCommand() {
        super("start", "Start command");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            absSender.execute(SendMessage.builder()
                    .chatId(chat.getId())
                    .text(GREETING_MESSAGE)
                    .build());
        } catch (Exception e) {
            log.error("Exception at start command {}: {}", chat.getId(), e.getMessage(), e);
        }
    }
}
