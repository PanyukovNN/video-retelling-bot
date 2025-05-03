package ru.panyukovnn.videoretellingbot.serivce.autodatafinder.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.serivce.autodatafinder.AutoDataFinder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class HabrDataFinder implements AutoDataFinder {

    private static final String JAVA_HUB = "java";

    @Override
    public List<String> findDataToLoad() {
        log.info("Начинаю поиск статей на habr за последние сутки");

        List<String> articleLinks = extractHabrArticleLinksFromHub(JAVA_HUB);

        log.info("Найдено статей: {}, в hub'е: {}", articleLinks.size(), JAVA_HUB);

        return articleLinks;
    }

    private List<String> extractHabrArticleLinksFromHub(String hub) {
        try {
            Document doc = Jsoup.connect("https://habr.com/ru/hubs/" + hub + "/articles/")
                .userAgent("Mozilla/5.0")
                .get();

            Elements articleBlocks = doc.select("div.tm-article-snippet");

            LocalDateTime dayBefore = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);

            return articleBlocks.stream()
                .takeWhile(article -> {
                    // Извлекаем и проверяем дату и время
                    String rawDateTime = article.selectFirst("time").attribute("datetime").getValue();

                    LocalDateTime parsedDateTime = ZonedDateTime.parse(rawDateTime).toLocalDateTime(); // в UTC

                    return parsedDateTime.isAfter(dayBefore);
                })
                .map(article -> {
                    // Извлекаем ссылку на статью
                    Element linkElement = article.selectFirst("a.tm-title__link");

                    return linkElement.absUrl("href");
                })
                .toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return List.of();
    }
}
