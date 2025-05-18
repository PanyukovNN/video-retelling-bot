package ru.panyukovnn.videoretellingbot.serivce.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.repository.ContentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentDomainService {

    private final ContentRepository contentRepository;

    public Content save(Content content) {
        return contentRepository.save(content);
    }

}
