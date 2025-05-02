package ru.panyukovnn.videoretellingbot.serivce.autodatafinder;

import java.util.List;

/**
 * Ищет статьи и видео для загрузки
 */
public interface AutoDataFinder {

    List<String> findDataToLoad();
}
