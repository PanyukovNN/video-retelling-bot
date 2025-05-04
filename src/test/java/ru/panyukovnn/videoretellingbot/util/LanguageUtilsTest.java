package ru.panyukovnn.videoretellingbot.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.videoretellingbot.model.loader.Lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LanguageUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "Привет, как дела?",
        "Ёжик ёлка"
    })
    void when_detectLangByLettersCount_withRusText_then_returnRuLang(String text) {
        assertEquals(Lang.RU, LanguageUtils.detectLangByLettersCount(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Hello, how are you?",
        "Hi Пр"
    })
    void when_detectLangByLettersCount_withEngText_then_returnEnLang(String text) {
        assertEquals(Lang.EN, LanguageUtils.detectLangByLettersCount(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234567890 !@#$%^&*()",
        "1234567890",
        "Hello!!! $$$"
    })
    void when_detectLangByLettersCount_withOtherSymbolsText_then_returnUndefinedLang(String text) {
        assertEquals(Lang.UNDEFINED, LanguageUtils.detectLangByLettersCount(text));
    }

    @Test
    void when_detectLangByLettersCount_withNullInput_then_returnUndefinedLang() {
        assertEquals(Lang.UNDEFINED, LanguageUtils.detectLangByLettersCount(null));
    }
}