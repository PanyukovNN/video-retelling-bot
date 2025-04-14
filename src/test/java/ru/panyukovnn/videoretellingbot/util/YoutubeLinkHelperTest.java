package ru.panyukovnn.videoretellingbot.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class YoutubeLinkHelperTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtu.be/dQw4w9WgXcQ",
        "https://m.youtube.com/watch?v=1Zr_ydPsmas&list=WL&index=6&ab_channel=MaximDorofeev",
        "https://www.m.youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtube.com/shorts/YOJ9yQx5ea4?si=Bjau-s_nZ-70Dkkw",
        "https://www.youtube.com/live/GNAiIFSwEGk?si=g1SSch43qU7eLohb"
    })
    void when_checkValidYoutubeLink_then_success(String validYoutubeLink) {
        YoutubeLinkHelper.checkYoutubeLink(validYoutubeLink);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/watch?v=abc123&feature=share&t=10",
        "https://www.youtube.com/watch?v=abc123",
        "https://www.youtube.com/watch?v=abc123&list=PLVe-2wcL84b&index=18&ab_channel=Some%2CThing%2FEncoded"
    })
    void shouldKeepOnlyVParam(String youtubeLink) {
        String actual = YoutubeLinkHelper.removeRedundantQueryParamsFromYoutubeLint(youtubeLink);
        assertEquals("https://www.youtube.com/watch?v=abc123", actual);
    }
}