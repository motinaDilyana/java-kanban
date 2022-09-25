package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private Integer uuid = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createTask(Task task) {
        task = new Task(uuid++, task.getName(), task.getDescription(), Statuses.NEW.toString());
        tasks.put(task.getUuid(), task);
        return task;
    }

    @Override
    public Task getTaskByUuid(Integer uuid) {
        historyManager.add(tasks.get(uuid));
        return tasks.get(uuid);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void update(Integer uuid, Task task) {
        tasks.put(uuid, task);
    }

    @Override
    public void deleteTask(Integer uuid) {
        tasks.remove(uuid);
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = new Epic(uuid++, epic.getName(), epic.getDescription(), Statuses.NEW.toString());
        epics.put(epic.getUuid(), epic);
        return epic;
    }

    @Override
    public Epic getEpicByUuid(Integer uuid) {
        historyManager.add(epics.get(uuid));
        return epics.get(uuid);
    }

    @Override
    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteEpic(Integer uuid) {
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicUuid().equals(uuid)) {
                subTasks.remove(entry.getKey());
            }
        }
        epics.remove(uuid);
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(Integer uuid) {
        ArrayList<SubTask> result = new ArrayList<>();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicUuid().equals(uuid)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    private String updateStatus(Epic epic) {
        ArrayList<Integer> epicSubTaskUuids = epic.getSubTaskUuids().isEmpty() ? new ArrayList<>() : epic.getSubTaskUuids();
        Integer doneCounter = 0;
        Integer newCounter = 0;

        String epicStatus = Statuses.NEW.toString();

        if (!epicSubTaskUuids.isEmpty()) {
            for (Object o : epicSubTaskUuids) {
                if (subTasks.containsKey(o)) {
                    SubTask task = subTasks.get(o);
                    if (task.getStatus().equals(Statuses.NEW.toString())) {
                        newCounter++;
                    } else if (task.getStatus().equals(Statuses.DONE.toString())) {
                        doneCounter++;
                    }
                }
            }

            if (doneCounter == 0) {
                epicStatus = Statuses.NEW.toString();
            } else if (newCounter == 0) {
                epicStatus = Statuses.DONE.toString();
            } else {
                epicStatus = Statuses.IN_PROGRESS.toString();
            }
        }
        return epicStatus;
    }

    @Override
    public void update(Integer uuid, Epic epic) {
        ArrayList<Integer> subTasksUuids = new ArrayList<>();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicUuid().equals(uuid)) {
                subTasksUuids.add(entry.getKey());
            }
        }

        String epicStatus = updateStatus(epic);
        epics.put(uuid, new Epic(uuid, epic.getName(), epic.getDescription(), epicStatus, subTasksUuids));
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> result = new ArrayList<>();

        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask = new SubTask(uuid++, subTask.getName(), subTask.getDescription(), Statuses.NEW.toString(), subTask.getEpicUuid());
        subTasks.put(subTask.getUuid(), subTask);
        update(subTask.getEpicUuid(), epics.get(subTask.getEpicUuid()));
        return subTask;
    }

    @Override
    public SubTask getSubTaskByUuid(Integer uuid) {
        historyManager.add(subTasks.get(uuid));
        return subTasks.get(uuid);
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            update(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void deleteSubTask(Integer uuid, Integer epicUuid) {
        subTasks.remove(uuid);
        update(epicUuid, epics.get(epicUuid));
    }

    @Override
    public void update(Integer uuid, SubTask subTask) {
        subTasks.put(uuid, subTask);
        update(subTask.getEpicUuid(), epics.get(subTask.getEpicUuid()));
    }

    @Override

    public LinkedList<Object> getHistory() {
        return historyManager.getHistory();
    }
}
