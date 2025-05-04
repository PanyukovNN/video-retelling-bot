package ru.panyukovnn.videoretellingbot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import ru.panyukovnn.videoretellingbot.model.loader.Lang;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageUtils {

    public static Lang detectLangByLettersCount(String text) {
        if (!StringUtils.hasText(text)) {
            return Lang.UNDEFINED;
        }

        int englishCount = 0;
        int russianCount = 0;
        int anotherSymbolsCount = 0;

        for (char c : text.toCharArray()) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                englishCount++;
            } else if ((c >= 'А' && c <= 'я') || c == 'ё' || c == 'Ё') {
                russianCount++;
            } else {
                anotherSymbolsCount++;
            }
        }

        if (anotherSymbolsCount > englishCount && anotherSymbolsCount > russianCount) {
            return Lang.UNDEFINED;
        }

        if (russianCount > englishCount) {
            return Lang.RU;
        }

        return Lang.EN;
    }

}
