package ru.panyukovnn.videoretellingbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.panyukovnn.videoretellingbot.client.OpenAiClient;
import ru.panyukovnn.videoretellingbot.config.TgBotApi;
import ru.panyukovnn.videoretellingbot.listener.TgBotListener;
import ru.panyukovnn.videoretellingbot.repository.ClientRepository;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.serivce.autodatafinder.impl.HabrDataFinder;
import ru.panyukovnn.videoretellingbot.serivce.loader.impl.HabrLoader;
import ru.panyukovnn.videoretellingbot.serivce.loader.impl.YoutubeSubtitlesLoader;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractTest {

    /**
     * Какие тесты надо написать:
     * - успешный сценарий обработки сообщения ботом
     * - успешный сценарий пересказа статьи
     */

    @Autowired
    protected HabrLoader habrLoader;
    @Autowired
    protected TgBotListener tgBotListener;
    @Autowired
    protected HabrDataFinder habrDataFinder;
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected ContentRepository contentRepository;

    @MockBean
    protected TgBotApi tgBotApi;
    @MockBean
    protected OpenAiClient openAiClient;
    @MockBean
    protected YoutubeSubtitlesLoader youtubeSubtitlesLoader;
}