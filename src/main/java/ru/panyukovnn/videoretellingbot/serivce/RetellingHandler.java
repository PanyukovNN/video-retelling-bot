package ru.panyukovnn.videoretellingbot.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;
import ru.panyukovnn.videoretellingbot.util.YoutubeLinkHelper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetellingHandler {

    private final TgSender tgSender;
    private final OpenAiClient openAiClient;
    private final YoutubeSubtitlesExtractor youtubeSubtitlesExtractor;

    public void handleRetelling(Long chatId, String inputMessage) {
        YoutubeLinkHelper.checkYoutubeLink(inputMessage);
        String cleanedYoutubeLink = YoutubeLinkHelper.removeRedundantQueryParamsFromYoutubeLint(inputMessage);

        tgSender.sendMessage(chatId, "Извлекаю содержание");

        String subtitles = youtubeSubtitlesExtractor.extractYoutubeVideoSubtitles(cleanedYoutubeLink);

        tgSender.sendMessage(chatId, "Формирую статью (это может занимать до 2х минут)");

        openAiClient.openAiCall(subtitles)
            .reduce(new StringBuilder(), StringBuilder::append)
            .map(StringBuilder::toString)
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
}
