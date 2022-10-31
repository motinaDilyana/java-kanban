package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task obj);

    void remove(Integer id);

    ArrayList<Task> getHistory();
}
