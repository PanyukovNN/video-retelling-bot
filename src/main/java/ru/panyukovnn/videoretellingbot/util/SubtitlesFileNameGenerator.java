package ru.panyukovnn.videoretellingbot.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class SubtitlesFileNameGenerator {
    
    public String generateFileName() {
        return "subtitles-" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
} 