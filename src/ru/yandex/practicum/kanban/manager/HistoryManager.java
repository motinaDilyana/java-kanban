package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public void add(Task task);

    public ArrayList<Task> getHistory();
}
