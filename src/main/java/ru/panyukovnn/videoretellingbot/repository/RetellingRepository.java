package ru.panyukovnn.videoretellingbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.videoretellingbot.model.retelling.Retelling;

import java.util.UUID;

public interface RetellingRepository extends JpaRepository<Retelling, UUID> {

}
