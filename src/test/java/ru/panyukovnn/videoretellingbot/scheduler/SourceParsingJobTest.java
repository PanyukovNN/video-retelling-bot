package ru.panyukovnn.videoretellingbot.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEventType;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingStatus;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.Source;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;
import ru.panyukovnn.videoretellingbot.repository.ProcessingEventRepository;
import ru.panyukovnn.videoretellingbot.serivce.autodatafinder.AutoDataFinder;
import ru.panyukovnn.videoretellingbot.serivce.loader.DataLoader;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SourceParsingJobTest {

    @Mock
    private DataLoader mockHabrLoader;
    @Mock
    private AutoDataFinder mockHabrDataFinder;
    @Mock
    private ContentRepository mockContentRepository;
    @Mock
    private ProcessingEventRepository mockProcessingEventRepository ;

    @InjectMocks
    private SourceParsingJob sourceParsingJob;

    @Test
    void when_parseHabr_then_success() {
        Content lastContent = new Content();
        lastContent.setLink("https://habr.com/article/1");
        lastContent.setSource(Source.HABR);

        List<String> foundedLinks = List.of(
            "https://habr.com/article/2",
            "https://habr.com/article/3"
        );

        Content newContent = new Content();
        newContent.setLink("https://habr.com/article/2");

        when(mockContentRepository.findTopBySourceOrderByPublicationDateDesc(Source.HABR))
            .thenReturn(Optional.of(lastContent));
        when(mockHabrDataFinder.findDataToLoad()).thenReturn(foundedLinks);
        when(mockHabrLoader.load("https://habr.com/article/2")).thenReturn(newContent);

        // Act
        sourceParsingJob.parseHabr();

        // Assert
        verify(mockHabrDataFinder).findDataToLoad();
        verify(mockHabrLoader).load("https://habr.com/article/2");
        verify(mockProcessingEventRepository).save(argThat(event ->
                event.getType() == ProcessingEventType.RATE_RAW_MATERIAL &&
                event.getStatus() == ProcessingStatus.NEW
        ));
    }

    @Test
    void when_parseHabr_withEmptyFoundedLinks_then_doNothing() {
        Content lastContent = new Content();
        lastContent.setLink("https://habr.com/article/1");
        lastContent.setSource(Source.HABR);

        List<String> emptyFoundedLinks = Collections.emptyList();

        when(mockContentRepository.findTopBySourceOrderByPublicationDateDesc(Source.HABR))
            .thenReturn(Optional.of(lastContent));
        when(mockHabrDataFinder.findDataToLoad()).thenReturn(emptyFoundedLinks);

        // Act
        sourceParsingJob.parseHabr();

        // Assert
        verify(mockHabrDataFinder).findDataToLoad();
        verify(mockHabrLoader, never()).load(anyString());
        verify(mockProcessingEventRepository, never()).save(any());
    }
}
