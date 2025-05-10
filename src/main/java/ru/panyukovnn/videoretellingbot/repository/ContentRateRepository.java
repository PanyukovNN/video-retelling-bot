package ru.panyukovnn.videoretellingbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.videoretellingbot.model.content.ContentRate;

import java.util.UUID;

public interface ContentRateRepository extends JpaRepository<ContentRate, UUID> {
}
