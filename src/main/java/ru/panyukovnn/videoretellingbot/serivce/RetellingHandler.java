package ru.panyukovnn.videoretellingbot.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static ru.panyukovnn.videoretellingbot.util.Constants.YOUTUBE_URL_REGEX;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetellingHandler {

    private final TgSender tgSender;
    private final OpenAiClient openAiClient;
    private final YoutubeSubtitlesExtractor youtubeSubtitlesExtractor;

    public void handleRetelling(Long chatId, String inputMessage) {
        checkYoutubeLink(inputMessage);

        tgSender.sendMessage(chatId, "Извлекаю содержание");

        String subtitles = youtubeSubtitlesExtractor.extractYoutubeVideoSubtitles(inputMessage);

        tgSender.sendMessage(chatId, "Формирую статью (это может занимать до 2х минут)");

        AtomicInteger paragraphsCount = new AtomicInteger();
        AtomicReference<StringBuilder> atomicReference = new AtomicReference<>(new StringBuilder());

        openAiClient.openAiCall(subtitles)
            .reduce(new StringBuilder(), StringBuilder::append)
            .map(StringBuilder::toString)
//            .handle((token, sink) -> {
//                    atomicReference.get().append(token);

//                if (atomicReference.get().toString().endsWith("\n\n")) {
//                    if (paragraphsCount.incrementAndGet() > 1) {
//                        sink.next(atomicReference.get().toString());
//                        atomicReference.set(new StringBuilder());
//                        paragraphsCount.set(0);
//                    }
//                }
//                }
//            })
            .doOnNext(videoSummary -> tgSender.sendMessage(chatId, videoSummary))
            .onErrorResume(e -> e instanceof RetellingException, e -> {
                log.error("Ошибка бизнес логики. id: {}. Сообщение: {}", ((RetellingException) e).getId(), e.getMessage(), e);

                tgSender.sendMessage(chatId, "В процессе работы возникла ошибка: " + e.getMessage());

                return Mono.empty();
            })
            .onErrorResume(e -> {
                log.error(e.getMessage(), e);

                tgSender.sendMessage(chatId, "Непредвиденная ошибка при отправке сообщения");

                return Mono.empty();
            })
            .subscribe(o -> log.info("Пересказ успешно выполнен и доставлен"));
    }

    protected void checkYoutubeLink(String messageText) {
        boolean validYoutubeVideoLink = Pattern.matches(YOUTUBE_URL_REGEX, messageText);

        if (!validYoutubeVideoLink) {
            throw new RetellingException("824c", "Невалидная ссылка youtube");
        }
    }
}
