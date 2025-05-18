package ru.panyukovnn.videoretellingbot.exception;

import lombok.Getter;

/**
 * При данном исключении ProcessingEvent подлжеит удалению
 */
@Getter
public class InvalidProcessingEventException extends RuntimeException {

    private final String id;

    public InvalidProcessingEventException(String id, String message) {
        super(message);
        this.id = id;
    }

    public InvalidProcessingEventException(String id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
