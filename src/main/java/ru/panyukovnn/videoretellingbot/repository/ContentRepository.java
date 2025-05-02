package ru.panyukovnn.videoretellingbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.videoretellingbot.model.loader.Content;
import ru.panyukovnn.videoretellingbot.model.loader.Source;

import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<Content, UUID> {

    Optional<Content> findTopBySourceOrderByPublicationDateDesc(Source source);

    Optional<Content> findByLink(String link);
}
