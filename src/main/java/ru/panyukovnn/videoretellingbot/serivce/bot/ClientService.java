package ru.panyukovnn.videoretellingbot.serivce.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.videoretellingbot.dto.UpdateParams;
import ru.panyukovnn.videoretellingbot.model.Client;
import ru.panyukovnn.videoretellingbot.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client save(UpdateParams updateParams) {
        Client client = clientRepository.findByTgUserId(updateParams.getUserId())
            .orElseGet(() -> Client.builder()
                .tgUserId(updateParams.getUserId())
                .tgLastChatId(updateParams.getChatId())
                .username(updateParams.getUserName())
                .firstname(updateParams.getFirstname())
                .lastname(updateParams.getLastname())
                .retellingsCount(0L)
                .build());

        client.setRetellingsCount(client.getRetellingsCount() + 1L);

        return clientRepository.save(client);
    }

}
