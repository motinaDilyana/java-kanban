package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if(taskHistory.size() >= 9) {
            taskHistory.remove(0);
        } else {
            taskHistory.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return this.taskHistory;
    }
}
