package ru.panyukovnn.videoretellingbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.videoretellingbot.model.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByTgUserId(Long tgUserId);
}
