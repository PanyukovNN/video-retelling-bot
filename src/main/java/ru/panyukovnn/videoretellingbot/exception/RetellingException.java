package ru.panyukovnn.videoretellingbot.exception;

import lombok.Getter;

@Getter
public class RetellingException extends RuntimeException {

    private String id;

    public RetellingException(String id, String message) {
        super(message);
    }

    public RetellingException(String id, String message, Throwable cause) {
        super(message, cause);
    }
}
