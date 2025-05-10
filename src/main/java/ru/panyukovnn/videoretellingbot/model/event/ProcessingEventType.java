package ru.panyukovnn.videoretellingbot.model.event;

public enum ProcessingEventType {

    /**
     * Задача на оценку полезности материала
     */
    RATE_RAW_MATERIAL,
    /**
     * Задача на пересказ
     */
    RETELLING,
    /**
     * Задача на публикацию пересказа
     */
    PUBLISH_RETELLING,
    /**
     * Опубликован, терминальный статус
     */
    PUBLISHED,
    /**
     * Ошибка публикации, терминальный статус
     */
    PUBLICATION_ERROR
}
