package ru.panyukovnn.videoretellingbot.serivce;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetellingHandlerTest {

    @InjectMocks
    private RetellingHandler retellingHandler;

    @ParameterizedTest
    @ValueSource(strings = {
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtu.be/dQw4w9WgXcQ",
        "https://m.youtube.com/watch?v=1Zr_ydPsmas&list=WL&index=6&ab_channel=MaximDorofeev",
        "https://www.m.youtube.com/watch?v=dQw4w9WgXcQ",
        "https://youtube.com/watch?v=dQw4w9WgXcQ"
    })
    void when_checkValidYoutubeLink_then_success(String validYoutubeLink) {
        retellingHandler.checkYoutubeLink(validYoutubeLink);
    }
}