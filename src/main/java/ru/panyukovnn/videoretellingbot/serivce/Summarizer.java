package ru.panyukovnn.videoretellingbot.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Summarizer {

    public static final String PROMPT = "Далее приведен текст, сделай его пересказ в виде статьи (можно использовать смайлики):\n\n";

    private final OpenAiChatModel chatModel;

    public String summarizeContent(String content) {
        log.info("Отправляю запрос в deepseek на получение краткого содержания");

        ChatResponse response = chatModel.call(new Prompt(PROMPT + content));

        log.info("Краткое содержание видео успешно сформировано");

        return response.getResult()
            .getOutput()
            .getText();
    }
}
