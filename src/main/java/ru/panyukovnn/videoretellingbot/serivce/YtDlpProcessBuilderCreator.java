package ru.panyukovnn.videoretellingbot.serivce;

import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;

import java.io.File;

/**
 * Данный класс вынесен отдельно для удобства тестирования
 */
@Service
public class YtDlpProcessBuilderCreator {

    public ProcessBuilder createProcessBuilder(String videoUrl, String lang, boolean isAutoSubs, String outputFileName) {
        return new ProcessBuilder(
            "./yt-dlp/" + defineYtDlpExecutableFileName(),
            isAutoSubs ? "--write-auto-subs" : "--write-subs",
            "--sub-lang", lang,
            "--sub-format", "vtt",
            "--skip-download",
            "-o", outputFileName,
            videoUrl
        )
            .directory(new File(".")) // Рабочая директория (куда сохраняются субтитры)
            .redirectErrorStream(true);
    }

    private String defineYtDlpExecutableFileName() {
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
