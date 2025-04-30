package ru.panyukovnn.videoretellingbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UpdateParams {

    private Long userId;
    private Long chatId;
    private String userName;
    private String firstname;
    private String lastname;
    private String input;
    private Instant date;
    private String callbackData;
}