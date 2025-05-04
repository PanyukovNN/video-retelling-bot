package ru.panyukovnn.videoretellingbot.testutils;

import lombok.SneakyThrows;
import org.springframework.data.repository.init.ResourceReader;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestFileUtil {

    @SneakyThrows
    public static String readFileFromResources(String fileName) {
        URI fileUri = Objects.requireNonNull(ResourceReader.class.getClassLoader().getResource(fileName)).toURI();

        try (Stream<String> stream = Files.lines(Paths.get(fileUri))) {
            return stream.collect(Collectors.joining("\n"));
        }
    }
}
