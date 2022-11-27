package ru.yandex.practicum.kanban.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public abstract class Managers {
    private static final String URL_TO_SERVER = "http://localhost:8085/";

    public static TaskManager getDefault() {
        return new HTTPTaskManager(URL_TO_SERVER);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
}
