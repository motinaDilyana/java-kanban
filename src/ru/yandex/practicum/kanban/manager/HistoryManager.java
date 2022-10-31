package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;

public interface HistoryManager<T extends Task> {
    void add(Task obj);

    void remove(Integer id);

    ArrayList<T> getHistory();
}
