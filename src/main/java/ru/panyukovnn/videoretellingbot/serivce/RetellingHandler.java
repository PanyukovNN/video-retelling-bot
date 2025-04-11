package ru.panyukovnn.videoretellingbot.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.property.RetellingProperties;
import ru.panyukovnn.videoretellingbot.serivce.telegram.TgSender;

import java.util.regex.Pattern;

import static ru.panyukovnn.videoretellingbot.util.Constants.YOUTUBE_URL_REGEX;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetellingHandler {

    private final TgSender tgSender;
    private final OpenAiClient openAiClient;
    private final RetellingProperties retellingProperties;
    private final YoutubeSubtitlesExtractor youtubeSubtitlesExtractor;

    public void handleRetelling(Long chatId, String inputMessage) {
        checkYoutubeLink(inputMessage);

        tgSender.sendMessage(chatId, "Извлекаю содержание");

        String subtitles = youtubeSubtitlesExtractor.extractYoutubeVideoSubtitles(inputMessage);

        tgSender.sendMessage(chatId, "Формирую статью (это может занимать до 2х минут)");

        String videoSummary = openAiClient.openAiCall(retellingProperties.getPromptPrefix() + "\n\n" + subtitles);

        tgSender.sendMessage(chatId, videoSummary);
    }

    protected void checkYoutubeLink(String messageText) {
        boolean validYoutubeVideoLink = Pattern.matches(YOUTUBE_URL_REGEX, messageText);

        if (!validYoutubeVideoLink) {
            throw new RetellingException("824c", "Невалидная ссылка youtube");
        }
    }
}
