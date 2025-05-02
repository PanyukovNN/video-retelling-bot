package ru.panyukovnn.videoretellingbot.serivce.loader;

import ru.panyukovnn.videoretellingbot.model.loader.Content;
import ru.panyukovnn.videoretellingbot.model.loader.Source;

public interface DataLoader {

    Content load(String link);

    Source getSource();
}
