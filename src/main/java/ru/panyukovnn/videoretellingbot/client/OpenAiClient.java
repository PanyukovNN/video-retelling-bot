package ru.panyukovnn.videoretellingbot.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.panyukovnn.videoretellingbot.util.BigTextUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiClient {

    public static final int WORDS_COUNT_THRESHOLD = 25000;

    private final OpenAiChatModel chatModel;

    public Flux<String> retellingCall(String prompt, String contentToRetell) {
        List<String> contentChunks = BigTextUtils.splitByWords(contentToRetell, WORDS_COUNT_THRESHOLD);

        return Flux.fromIterable(contentChunks)
            .flatMap(contentChunk -> call(prompt, contentChunk));
    }

    public String retellingBlockingCall(String requestType, String prompt, String contentToRetell) {
        return blockingCall(requestType, prompt, contentToRetell);
    }

    private Flux<String> call(String prompt, String contentToRetell) {
        log.info("Отправляю запрос в AI");

        return chatModel.stream(new Prompt(prompt + "\n\n" + contentToRetell))
                .map(chatResponse -> chatResponse.getResult().getOutput().getText());
    }

    private String blockingCall(String requestType, String prompt, String contentToRetell) {
        log.info("Отправляю запрос в AI для: {}", requestType);

        return chatModel.call(new Prompt(prompt + "\n\n" + contentToRetell))
            .getResult()
            .getOutput()
            .getText();
    }
}
