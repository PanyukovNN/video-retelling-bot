package ru.panyukovnn.videoretellingbot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.panyukovnn.videoretellingbot.exception.RetellingException;
import ru.panyukovnn.videoretellingbot.serivce.Summarizer;
import ru.panyukovnn.videoretellingbot.serivce.TgSender;
import ru.panyukovnn.videoretellingbot.serivce.YoutubeSubtitlesExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotListener {

    private static final int MAX_TG_MESSAGE_SIZE = 4096;
    private static final String YOUTUBE_URL_REGEX = "^(https://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}(&.+)?$";

    private final TgSender tgSender;
    private final Summarizer summarizer;
    private final YoutubeSubtitlesExtractor youtubeSubtitlesExtractor;

    @EventListener(Update.class)
    @Async("tgListenerExecutor")
    public void onUpdate(Update update) {
        Long userId = Optional.ofNullable(update.getMessage())
            .map(Message::getFrom)
            .map(User::getId)
            .orElse(0L);
        String messageText = Optional.ofNullable(update.getMessage())
            .map(Message::getText)
            .map(String::trim)
            .orElse("Не удалось извлечь текст сообщения");

        log.info("Received message from user: {}. Text: {}", userId, messageText);

        Long chatId = Optional.ofNullable(update.getMessage())
            .map(Message::getChatId)
            .orElseThrow();

        try {
            checkYoutubeLink(messageText);

            tgSender.sendMessage(chatId, "Извлекаю содержание");

            String subtitles = youtubeSubtitlesExtractor.extractYoutubeVideoSubtitles(messageText);

            tgSender.sendMessage(chatId, "Формирую статью (это может занимать до 2х минут)");

            String videoSummary = summarizer.summarizeContent(subtitles);
            String cleanedSummary = changeHashSignsToBoldInMarkdown(videoSummary);

            log.info(cleanedSummary);

            List<String> videoSummarySubMessages = splitTooLongMessage(cleanedSummary);

            videoSummarySubMessages.forEach(videoSummarySubMessage -> tgSender.sendMessage(chatId, videoSummarySubMessage));
        } catch (RetellingException e) {
            log.error("Ошибка бизнес логики. id: {}. Сообщение: {}", e.getId(), e.getMessage(), e);

            tgSender.sendMessage(chatId, "В процессе работы возникла ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            tgSender.sendMessage(chatId, "Непредвиденная ошибка при отправке сообщения");
        }
    }

    private void checkYoutubeLink(String messageText) {
        boolean validYoutubeVideoLink = Pattern.matches(YOUTUBE_URL_REGEX, messageText);

        if (!validYoutubeVideoLink) {
            throw new RetellingException("824c", "Невалидная ссылка youtube");
        }
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

//# 📌 Три ключевые сущности самоорганизации: задачи, проекты и идеи
//
//Привет, друзья! 👋 Сегодня разберёмся с основами самоорганизации. Ведь часто мы путаем **задачи**, **проекты** и **идеи**, сваливая всё в одну кучу. А потом удивляемся, почему ничего не делается! 😅
//
//## 🧠 Как работает наш мозг?
//
//Прежде чем перейти к сущностям, вспомним модель **Тима Урбана** (если не знаете, кто это — гуглите его выступление на TED 🎤).
//
//🔹 **Обезьянка** — импульсивная часть мозга, которая хочет удовольствий здесь и сейчас.
//🔹 **Рациональный тип** — наш "взрослый" режим, который умеет планировать.
//🔹 **Панический монстр** — включается, когда дедлайн уже на носу!
//
//Большую часть времени мы живём в режиме **обезьянки** — неосознанно, импульсивно. Поэтому важно правильно организовать информацию, чтобы даже в таком состоянии мы могли эффективно действовать.
//
//---
//
//## 📋 1. **Задачи** — "Надо сделать, не думать!"
//
//✅ **Определение**: То, что уже продумано и осталось только выполнить.
//✅ **Критерии хорошей задачи**:
//✔ **Конкретность** (с глаголом! "Написать пост", а не "Блог").
//✔ **Свежесть** (не старше недели, иначе пересматриваем).
//✔ **Простота** (не больше 1–2 шагов).
//
//❌ **Ошибки**:
//- Делать список из 300 задач (обезьянка сбежит 🏃).
//- Путать задачи с проектами (например, "Разработать сайт" — это проект, а "Написать текст для главной" — задача).
//
//💡 **Совет**: Если задача висит дольше недели — либо переформулируйте, либо вычеркните.
//
//---
//
//## 🏗 2. **Проекты** — "Надо сделать, но сначала подумать КАК"
//
//✅ **Определение**: Что-то сложное, требующее разбивки на шаги.
//✅ **Как работать**:
//✔ Хранить **отдельно от задач** (обезьянка боится сложного).
//✔ Раз в день просматривать в **осознанном состоянии**.
//✔ Выделять **1–2 задачи** для обезьянки (не расписывать всё до мелочей).
//
//❌ **Ошибка**: Декомпозировать проект на 50 задач сразу (жизнь изменится, и половина станет неактуальной).
//
//---
//
//## 💡 3. **Идеи** — "Надо подумать, а стоит ли вообще делать?"
//
//✅ **Определение**: То, что звучит круто, но не факт, что нужно вам.
//✅ **Как работать**:
//✔ Хранить **отдельно** (не в задачах!).
//✔ Периодически пересматривать в **творческом состоянии**.
//✔ Тестировать перед внедрением (например, прочитать фрагмент книги, а не сразу покупать).
//
//❌ **Ошибка**: Записывать идеи как задачи ("Прочитать 50 книг" → чувство вины 😞).
//
//---
//
//## 🔄 Что ещё важно?
//
//📅 **Ежедневный чек-лист** ≠ список задач. Это **наблюдение за привычками**, а не обязательства.
//
//🗂 **Справочная информация** — тема для отдельного разговора (но главное — не смешивать с задачами).
//
//---
//
//### 🎯 **Вывод**
//- **Задачи** = делай без раздумий.
//- **Проекты** = думай, потом делай.
//- **Идеи** = думай, а надо ли?
//
//Разделяйте эти сущности — и ваша продуктивность взлетит! 🚀
//
//А как вы организуете свои дела? Делитесь в комментариях! 👇😊