package ru.panyukovnn.videoretellingbot.serivce.loader;

import ru.panyukovnn.videoretellingbot.model.content.Content;
import ru.panyukovnn.videoretellingbot.model.content.Source;

public interface DataLoader {

    Content load(String link);

    Source getSource();
}
