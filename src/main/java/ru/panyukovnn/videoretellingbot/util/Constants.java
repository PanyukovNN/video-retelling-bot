package ru.panyukovnn.videoretellingbot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final int MAX_TG_MESSAGE_SIZE = 4096;
    public static final String YOUTUBE_URL_REGEX = "^(https://)?(www\\.|m\\.|www\\.m\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}([&?].+)?$";
}
