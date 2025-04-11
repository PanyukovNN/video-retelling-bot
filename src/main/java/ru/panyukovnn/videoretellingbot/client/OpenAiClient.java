package ru.panyukovnn.videoretellingbot.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiClient {

    private final OpenAiChatModel chatModel;

    public String openAiCall(String prompt) {
        log.info("Отправляю запрос в AI");

        ChatResponse response = chatModel.call(new Prompt(prompt));

        log.info("Ответ от AI успешно получен");

        return response.getResult()
            .getOutput()
            .getText();
    }
}
