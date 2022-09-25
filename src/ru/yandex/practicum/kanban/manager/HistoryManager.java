package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.LinkedList;

public interface HistoryManager {
    void add(Object obj);

    LinkedList<Object> getHistory();
}
