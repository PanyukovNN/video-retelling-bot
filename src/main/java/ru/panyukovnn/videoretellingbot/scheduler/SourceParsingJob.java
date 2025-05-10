package ru.panyukovnn.videoretellingbot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingStatus;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.Source;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.serivce.autodatafinder.AutoDataFinder;
import ru.panyukovnn.videoretellingbot.serivce.loader.DataLoader;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceParsingJob {

    private final DataLoader habrLoader;
    private final AutoDataFinder habrDataFinder;
    private final ContentRepository contentRepository;
    private final ProcessingEventRepository processingEventRepository;

    @Async("sourceParsingScheduler")
    @Scheduled(cron = "${retelling.scheduled-jobs.source-parsing.habr-cron}")
    public void parseHabr() {
        try {
            Content lastContent = contentRepository.findTopBySourceOrderByPublicationDateDesc(Source.HABR)
                .orElse(null);
            List<String> foundedLinks = habrDataFinder.findDataToLoad();

            List<String> linksToLoad = defineLinksToLoad(foundedLinks, lastContent);

            linksToLoad.forEach(link -> {
                try {
                    Content content = habrLoader.load(link);

                    if (!processingEventRepository.existsByContentId(content.getId())) {
                        processingEventRepository.save(ProcessingEvent.builder()
                            .type(ProcessingEventType.RATE_RAW_MATERIAL)
                            .contentId(content.getId())
                            .status(ProcessingStatus.NEW)
                            .build());
                    }
                } catch (Exception e) {
                    log.error("Ошибка при загрузке содержимого статьи с habr: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error("Ошибка при пасниге habr: {}", e.getMessage(), e);
        }
    }

    protected List<String> defineLinksToLoad(List<String> foundedLinks, Content lastContent) {
        if (lastContent != null && foundedLinks.contains(lastContent.getLink())) {
            return foundedLinks.stream()
                .dropWhile(link -> !link.equals(lastContent.getLink()))
                .toList();
        }

        return foundedLinks;
    }
}
