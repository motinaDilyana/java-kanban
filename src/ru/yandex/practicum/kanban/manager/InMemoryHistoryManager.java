package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.util.CustomLinkedList;
import ru.yandex.practicum.kanban.manager.util.Node;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryHistoryManager<T> implements HistoryManager{
    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();
    private final Map<Integer, Node<T>> taskLocation = new HashMap<>();

    @Override
    public void add(Task task) throws NullTaskException{
            if (Objects.isNull(task)) {
                throw new NullTaskException("Task не может быть пустым");
            }
            Node<T> node = taskLocation.get(task.getUuid());
            if (Objects.nonNull(node)) {
                taskHistory.removeNode(node);
            }
            Node lastNode = taskHistory.linkLast(task);
            taskLocation.put(task.getUuid(), lastNode);
    }

    @Override
    public void remove(Integer id) {
        Node<T> node = taskLocation.get(id);
        if(Objects.nonNull(node)) {
            taskHistory.removeNode(node);
            taskLocation.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory.getTasks();
    }
}
