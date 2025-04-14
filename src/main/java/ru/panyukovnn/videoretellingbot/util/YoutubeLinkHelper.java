package ru.panyukovnn.videoretellingbot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import static ru.panyukovnn.videoretellingbot.util.Constants.YOUTUBE_URL_REGEX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YoutubeLinkHelper {

    public static String removeRedundantQueryParamsFromYoutubeLint(String youtubeLink) {
        try {
            URI uri = new URI(youtubeLink);
            String query = uri.getRawQuery();

            if (query == null || query.isEmpty()) {
                return youtubeLink;
            }

            String vValue = null;
            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2 && pair[0].equals("v")) {
                    vValue = pair[1];
                    break;
                }
            }

            URI cleanedUri = new URI(
                uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                vValue != null ? "v=" + vValue : null,
                uri.getFragment()
            );

            return cleanedUri.toString();
        } catch (URISyntaxException e) {
            throw new RetellingException("4bc5", "Невалидная ссылка youtube", e);
        }
    }

    public static void checkYoutubeLink(String messageText) {
        boolean validYoutubeVideoLink = Pattern.matches(YOUTUBE_URL_REGEX, messageText);

        if (!validYoutubeVideoLink) {
            throw new RetellingException("824c", "Невалидная ссылка youtube");
        }
    }
}
