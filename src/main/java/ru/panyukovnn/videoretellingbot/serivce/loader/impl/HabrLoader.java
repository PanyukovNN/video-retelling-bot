package ru.panyukovnn.videoretellingbot.serivce.loader.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.model.loader.Content;
import ru.panyukovnn.videoretellingbot.model.loader.ContentType;
import ru.panyukovnn.videoretellingbot.model.loader.Lang;
import ru.panyukovnn.videoretellingbot.model.loader.Source;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.serivce.loader.DataLoader;
import ru.panyukovnn.videoretellingbot.util.LanguageUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabrLoader implements DataLoader {

    private final ContentRepository contentRepository;

    @Override
    public Content load(String link) {
        try {
            Content existingContentByLink = contentRepository.findByLink(link)
                .orElse(null);

            if (existingContentByLink != null) {
                return existingContentByLink;
            }

            Document doc = Jsoup.connect(link)
                .userAgent("Mozilla/5.0")
                .get();

            String title = doc.select("h1.tm-title > span").text();
            String rawDateTime = doc.select("span.tm-article-datetime-published > time").attr("datetime");
            String articleContent = doc.select("div.tm-article-body").text();

            LocalDateTime parsedDateTime = ZonedDateTime.parse(rawDateTime).toLocalDateTime();

            Content content = Content.builder()
                .link(link)
                .lang(LanguageUtils.detectLangByLettersCount(articleContent))
                .type(ContentType.ARTICLE)
                .source(getSource())
                .title(title)
                .publicationDate(parsedDateTime)
                .content(articleContent)
                .build();

            log.info("Загружена статья с habr: {}. Ссылка: {}. Дата публикации: {}", title, link, parsedDateTime);

            return contentRepository.save(content);
        } catch (Exception e) {
            log.error(e.getMessage() ,e);

            throw new RetellingException("ada9", "Не удалось загрузить статью с Habr", e);
        }
    }

    @Override
    public Source getSource() {
        return Source.HABR;
    }
}
