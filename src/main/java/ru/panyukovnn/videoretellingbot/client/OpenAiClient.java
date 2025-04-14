package ru.panyukovnn.videoretellingbot.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.panyukovnn.videoretellingbot.property.RetellingProperties;
import ru.panyukovnn.videoretellingbot.util.BigTextUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiClient {

    public static final int WORDS_COUNT_THRESHOLD = 25000;

    private final OpenAiChatModel chatModel;
    private final RetellingProperties retellingProperties;

    public Flux<String> openAiCall(String contentToRetell) {
        List<String> contentChunks = BigTextUtils.splitByWords(contentToRetell, WORDS_COUNT_THRESHOLD);

        return Flux.fromIterable(contentChunks)
            .flatMap(this::call);
    }

    private Flux<String> call(String contentToRetell) {
        log.info("Отправляю запрос в AI");

        return chatModel.stream(new Prompt(retellingProperties.getPromptPrefix() + "\n\n" + contentToRetell))
                .map(chatResponse -> chatResponse.getResult().getOutput().getText());
    }
}
