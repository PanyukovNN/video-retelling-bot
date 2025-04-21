package ru.panyukovnn.videoretellingbot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final int MAX_TG_MESSAGE_SIZE = 4096;
    public static final String YOUTUBE_URL_REGEX = "^(https://)?(www\\.|m\\.|www\\.m\\.)?(youtube\\.com|youtu\\.be)/(watch\\?.*?[?&]v=([\\w-]{11})|shorts/([\\w-]{11})|live/([\\w-]{11})|([\\w-]{11}))([?&].*)?$";
    public static final Pattern YOUTUBE_VIDEO_ID_PATTERN = Pattern.compile("^[\\w-]{11}$");
}
