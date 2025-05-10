package ru.panyukovnn.videoretellingbot.exception;

import lombok.Getter;

@Getter
public class RawMaterialRateException extends RuntimeException {

    private final String id;

    public RawMaterialRateException(String id, String message) {
        super(message);
        this.id = id;
    }

    public RawMaterialRateException(String id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
