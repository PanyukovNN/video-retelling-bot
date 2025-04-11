package ru.panyukovnn.videoretellingbot.exception;

import lombok.Getter;

@Getter
public class RetellingException extends RuntimeException {

    private final String id;

    public RetellingException(String id, String message) {
        super(message);
        this.id = id;
    }

    public RetellingException(String id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
