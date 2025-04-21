package ru.panyukovnn.videoretellingbot.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

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
        "https://www.youtube.com/live/GNAiIFSwEGk?si=g1SSch43qU7eLohb",
        "https://m.youtube.com/watch?time_continue=140&v=ACf59g4ItDE&embeds_referring_euri=https%3A%2F%2Fwww.google.com%2F&source_ve_path=MjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjgyNDAsMjg2NjY"
    })
    void when_checkValidYoutubeLink_then_success(String validYoutubeLink) {
        assertTrue(YoutubeLinkHelper.isValidYoutubeUrl(validYoutubeLink));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.wrongyoutube.com/watch?v=dQw4w9WgXcQ",
        "https://youtu.bo/dQw4w9WgXcQ",
        "https://.youtube.com/watch?v=1Zr_ydPsmas&list=WL&index=6&ab_channel=MaximDorofeev",
        "https://www.m.youtube.com/watch?v=dQw4w9WgX",
        "https://youtube.com/atch?v=dQw4w9WgXcQ",
        "https://google.com/shorts/YOJ9yQx5ea4?si=Bjau-s_nZ-70Dkkw",
        "https://www.youtube.com/live/"
    })
    void when_checkValidYoutubeLink_urlInvalid_then_success(String validYoutubeLink) {
        assertFalse(YoutubeLinkHelper.isValidYoutubeUrl(validYoutubeLink));
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