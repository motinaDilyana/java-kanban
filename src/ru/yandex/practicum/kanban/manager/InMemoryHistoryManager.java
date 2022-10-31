package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.manager.util.CustomLinkedList;
import ru.yandex.practicum.kanban.manager.util.Node;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager<T> implements HistoryManager{
    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();
    private final Map<Integer, Node<T>> taskLocation = new HashMap<>();

    @Override
    public void add(Task task) {
            Node<T> node = taskLocation.get(task.getUuid());
            if (node != null) {
                taskHistory.removeNode(node);
            }
            Node lastNode = taskHistory.linkLast(task);
            taskLocation.put(task.getUuid(), lastNode);
    }

    @Override
    public void remove(Integer id) {
        Node<T> node = taskLocation.get(id);
        if(node != null) {
            taskHistory.removeNode(node);
            taskLocation.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory.getTasks();
    }
}
