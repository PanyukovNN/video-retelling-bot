package ru.panyukovnn.videoretellingbot.serivce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class YoutubeSubtitlesExtractor {

    private static final String SUBTITLES_LANG = "ru";
    public static final String SUBTITLES_DIRECTORY = "./subtitles/";

    public String extractYoutubeVideoSubtitles(String videoUrl) {
        log.info("Начинаю загрузку субтитров из youtube видео по ссылке: {}", videoUrl);

        cleanSubtitlesFolder();

        try {
            String subtitlesFilename = downloadSubtitles(videoUrl);

            File subtitlesFile = new File(SUBTITLES_DIRECTORY + subtitlesFilename);

            List<String> strings = Files.readAllLines(subtitlesFile.toPath());
            Set<String> cleanedFileLines = cleanSubtitles(strings);

            return String.join("\n", cleanedFileLines);
        } catch (Exception e) {
            log.error("Ошибка загрузки субтитров из видео: {}", e.getMessage(), e);

            throw new RetellingException("63e9", "Не удалось извлечь субтитры из видео");
        }
    }

    private void cleanSubtitlesFolder() {
        long cutoffMillis = Instant.now().minusSeconds(3600).toEpochMilli();

        File folder = new File(SUBTITLES_DIRECTORY);

        File[] files = folder.listFiles();
        if (files == null) {
            log.warn("Не удалось прочитать содержимое папки с файлами субтитров, для очистки.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                if (file.lastModified() < cutoffMillis) {
                    boolean deleted = file.delete();

                    log.info("Файл: {} {}", file.getName(), (deleted ? "удалён" : "не удалось удалить"));
                }
            }
        }
    }

    /**
     * @param videoUrl ссылка на видео на youtube
     * @return имя файла с субтитрами, куда произошла выгрузка
     */
    private String downloadSubtitles(String videoUrl) {
        String outputFileName = "subtitles-" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "-" + UUID.randomUUID().toString().substring(0, 8);

        ProcessBuilder builder = new ProcessBuilder(
            "./yt-dlp/" + defineYtDlpExecutableFileName(),
            "--write-auto-subs",
            "--sub-lang", SUBTITLES_LANG,
            "--sub-format", "vtt",
            "--skip-download",
            "-o", SUBTITLES_DIRECTORY + outputFileName,
            videoUrl
        );

        // Рабочая директория (куда сохраняются субтитры)
        builder.directory(new File("."));
        builder.redirectErrorStream(true);

        try {
            log.info("Начало загрузки субтитров для видео: {}", videoUrl);

            Process process = builder.start();

            // Чтение вывода yt-dlp
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RetellingException("12d7", "Ошибка выгрузки субтитров с помощью yt-dlp, exitCode: " + exitCode);
            }
        } catch (Exception e) {
            throw new RetellingException("45bb", "Ошибка выгрузки субтитров с помощью yt-dlp: " + e.getMessage(), e);
        }

        log.info("Субтитры успешо загружены");

        return outputFileName + "." + SUBTITLES_LANG + ".vtt";
    }

    private Set<String> cleanSubtitles(List<String> lines) {
        Set<String> uniqueLines = new LinkedHashSet<>();

        for (String line : lines) {
            // Убираем теги и метки
            String cleanedLine = line
                .replaceAll("<[^>]+>", "")                         // Удаляет все теги вида <...>
                .replaceAll("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}", "")   // Удаляет временные метки
                .replaceAll("-->.*", "")                           // Удаляет строки с временными интервалами
                .replaceAll("align:\\w+ position:\\d+%", "")       // Удаляет служебные параметры
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")         // Удаляет управляющие символы
                .replaceAll("\\s{2,}", " ")                        // Заменяет множественные пробелы на один
                .trim();

            if (!cleanedLine.isEmpty()) {
                uniqueLines.add(cleanedLine);
            }
        }

        return uniqueLines;
    }

    private static String defineYtDlpExecutableFileName() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            return "yt-dlp_macos";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            String arch = System.getProperty("os.arch").toLowerCase();

            return arch.contains("aarch64") || arch.contains("arm64")
                ? "yt-dlp_linux_aarch64"
                : "yt-dlp_linux";
        }

        throw new RetellingException("4824", "Не удалось определить подходящий yt-dlp исполняемый файл для системы: " + osName);
    }
}
