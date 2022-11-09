package ru.yandex.practicum.kanban.manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackendTaskManager(new File("src/ru/yandex/practicum/kanban/tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
