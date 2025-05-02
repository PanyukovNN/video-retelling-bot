package ru.panyukovnn.videoretellingbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.videoretellingbot.model.event.ProcessingEvent;

import java.util.UUID;

public interface ProcessingEventRepository extends JpaRepository<ProcessingEvent, UUID> {

    boolean existsByContentId(UUID contentId);
}
