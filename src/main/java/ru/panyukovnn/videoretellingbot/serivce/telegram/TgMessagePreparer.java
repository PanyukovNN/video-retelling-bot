package ru.panyukovnn.videoretellingbot.serivce.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ru.panyukovnn.videoretellingbot.util.Constants.MAX_TG_MESSAGE_SIZE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgMessagePreparer {

    public List<String> prepareLongTgMessage(String content) {
        String cleanedSummary = changeHashSignsToBoldInMarkdown(content);

        List<String> splitLongMessages = splitTooLongMessage(cleanedSummary);

        splitLongMessages.forEach(log::info);

        return splitLongMessages;
    }

    protected List<String> splitTooLongMessage(String message) {
        List<String> tgMessages = new ArrayList<>();

        String[] lines = message.split("\n", -1); // -1 сохраняет пустые строки

        StringBuilder currentChunk = new StringBuilder();

        for (String line : lines) {
            String lineWithNewline = line + "\n";

            if (currentChunk.length() + lineWithNewline.length() > MAX_TG_MESSAGE_SIZE) {
                tgMessages.add(currentChunk.toString());
                currentChunk.setLength(0);
            }

            currentChunk.append(lineWithNewline);
        }

        if (!currentChunk.isEmpty()) {
            tgMessages.add(currentChunk.toString());
        }

        return tgMessages;
    }

    protected String changeHashSignsToBoldInMarkdown(String input) {
        String[] lines = input.split("\n", -1); // -1 сохраняет пустые строки

        StringBuilder output = new StringBuilder();

        for (String line : lines) {
            String lineWithNewline = line + "\n";

            if (lineWithNewline.startsWith("#")) {
                String cleaned = lineWithNewline.replaceFirst("^#+", "").trim();
                output.append(cleaned);
            } else {
                output.append(lineWithNewline);
            }
        }

        return output.toString();
    }
}
