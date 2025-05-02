package ru.panyukovnn.videoretellingbot.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.model.loader.Content;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.serivce.loader.DataLoader;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;
import ru.panyukovnn.videoretellingbot.util.YoutubeLinkHelper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotRetellingHandler {

    private final TgSender tgSender;
    private final OpenAiClient openAiClient;
    private final DataLoader youtubeSubtitlesLoader;
    private final ContentRepository contentRepository;

    public void handleRetelling(Long chatId, String inputMessage) {
        if (!YoutubeLinkHelper.isValidYoutubeUrl(inputMessage)) {
            throw new RetellingException("824c", "Невалидная ссылка youtube: " + inputMessage);
        }

        String cleanedYoutubeLink = YoutubeLinkHelper.removeRedundantQueryParamsFromYoutubeLint(inputMessage);

        tgSender.sendMessage(chatId, "Извлекаю содержание");

        Content content = youtubeSubtitlesLoader.load(cleanedYoutubeLink);

        contentRepository.save(content);

        String subtitles = content.getContent();

        tgSender.sendMessage(chatId, "Формирую статью (это может занимать до 2х минут)");

        openAiClient.retellingCall(subtitles)
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
