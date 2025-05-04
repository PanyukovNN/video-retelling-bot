package ru.panyukovnn.videoretellingbot.serivce.autodatafinder.impl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.panyukovnn.videoretellingbot.AbstractTest;
import ru.panyukovnn.videoretellingbot.testutils.TestFileUtil;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HabrDataFinderTest extends AbstractTest {

    @Test
    public void when_findDataToLoad_then_success() throws IOException {
        String habrJavaHubPage = TestFileUtil.readFileFromResources("mockdata/service/autodatafinder/habr/habr-java-articles-hub.html");

        Document javaHubPageDoc = Jsoup.parse(habrJavaHubPage);

        try (MockedStatic<Jsoup> jsoupMock = Mockito.mockStatic(Jsoup.class)) {
            Connection connectionMock = Mockito.mock(Connection.class);
            jsoupMock.when(() -> Jsoup.connect("https://habr.com/ru/hubs/java/articles/"))
                .thenReturn(connectionMock);
            Mockito.when(connectionMock.userAgent("Mozilla/5.0")).thenReturn(connectionMock);
            Mockito.when(connectionMock.get()).thenReturn(javaHubPageDoc);

            List<String> articleLinks = habrDataFinder.findDataToLoad();

            assertThat(articleLinks)
                .hasSize(20)
                .contains("https://habr.com/ru/articles/906318/",
                    "https://habr.com/ru/articles/905910/",
                    "https://habr.com/ru/companies/spring_aio/articles/905902/",
                    "https://habr.com/ru/companies/spring_aio/articles/905586/",
                    "https://habr.com/ru/companies/simbirsoft/articles/903686/",
                    "https://habr.com/ru/companies/haulmont/articles/905038/",
                    "https://habr.com/ru/companies/axiomjdk/articles/904928/",
                    "https://habr.com/ru/articles/904954/",
                    "https://habr.com/ru/articles/904952/",
                    "https://habr.com/ru/articles/904766/",
                    "https://habr.com/ru/articles/904632/",
                    "https://habr.com/ru/articles/904554/",
                    "https://habr.com/ru/companies/pvs-studio/articles/904438/",
                    "https://habr.com/ru/companies/spring_aio/articles/903864/",
                    "https://habr.com/ru/companies/ru_mts/articles/902544/",
                    "https://habr.com/ru/companies/spring_aio/articles/903856/",
                    "https://habr.com/ru/companies/spring_aio/articles/903542/",
                    "https://habr.com/ru/articles/902922/",
                    "https://habr.com/ru/articles/902854/",
                    "https://habr.com/ru/companies/otus/articles/902240/");
        }
    }
}