package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {

    private Integer uuid = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createTask(Task task) throws NullTaskException {
        if(Objects.isNull(task)) {
            throw new NullTaskException("Task не может быть пустым.");
        }
        task = new Task(uuid++, task.getName(), task.getDescription(), Statuses.NEW.toString());
        tasks.put(task.getUuid(), task);
        return task;
    }

    @Override
    public Task getTaskByUuid(Integer uuid) throws TaskNotFoundException {
        final Task task = tasks.get(uuid);
        if(Objects.isNull(task)) {
            throw new TaskNotFoundException("Task с uuid "+ uuid + " не найден");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void update(Integer uuid, Task task) throws TaskNotFoundException {
        if(Objects.isNull(task) || Objects.isNull(uuid)) {
            throw new TaskNotFoundException("Task с uuid "+ uuid + " не найден");
        }
        tasks.put(uuid, task);
    }

    @Override
    public void deleteTask(Integer uuid) throws TaskNotFoundException {
        Task task = tasks.get(uuid);
        if(Objects.isNull(task) || Objects.isNull(uuid)) {
            throw new TaskNotFoundException("Task с uuid "+ uuid + " не найден");
        }
        historyManager.remove(uuid);
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
    public Epic createEpic(Epic epic) throws NullTaskException {
        if(Objects.isNull(epic)) {
            throw new NullTaskException("Epic не может быть пустым.");
        }
        epic = new Epic(uuid++, epic.getName(), epic.getDescription(), Statuses.NEW.toString());
        epics.put(epic.getUuid(), epic);
        return epic;
    }

    @Override
    public Epic getEpicByUuid(Integer uuid) throws TaskNotFoundException, NullTaskException {
        final Epic epic = epics.get(uuid);

        if(Objects.isNull(epic) || Objects.isNull(uuid)) {
            throw new TaskNotFoundException("Epic с uuid "+ uuid + " не найден");
        }

        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteEpic(Integer uuid) throws TaskNotFoundException {
        if(Objects.isNull(uuid)) {
            throw new TaskNotFoundException("Epic с uuid "+ uuid + " не найден");
        }
        Map<Integer, SubTask> copyMap = new HashMap<>(subTasks);
        for (Map.Entry<Integer, SubTask> entry : copyMap.entrySet()) {
            if (entry.getValue().getEpicUuid().equals(uuid)) {
                historyManager.remove(entry.getKey());
                subTasks.remove(entry.getKey());
            }
        }
        historyManager.remove(uuid);
        epics.remove(uuid);
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(Integer uuid) throws TaskNotFoundException {
        if(Objects.isNull(uuid)) {
            throw new TaskNotFoundException("Epic с uuid "+ uuid + " не найден");
        }
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

    private String updateStatus(Epic epic) throws NullTaskException{
        if(Objects.isNull(epic)) {
            throw new NullTaskException("Epic не может быть пустым");
        }
        ArrayList<Integer> epicSubTaskUuids = epic.getSubTaskUuids().isEmpty() ? new ArrayList<>() : epic.getSubTaskUuids();
        int doneCounter = 0;
        int newCounter = 0;

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
    public void update(Integer uuid, Epic epic) throws TaskNotFoundException {
        if(Objects.isNull(epic) || Objects.isNull(uuid)) {
            throw new TaskNotFoundException("Epic с uuid "+ uuid + " не найден");
        }
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
    public SubTask createSubTask(SubTask subTask) throws NullTaskException{
        if(Objects.isNull(subTask)) {
            throw new NullTaskException("SubTask не может быть пустым.");
        }
        subTask = new SubTask(uuid++, subTask.getName(), subTask.getDescription(), Statuses.NEW.toString(), subTask.getEpicUuid());
        subTasks.put(subTask.getUuid(), subTask);
        update(subTask.getEpicUuid(), epics.get(subTask.getEpicUuid()));
        return subTask;
    }

    @Override
    public SubTask getSubTaskByUuid(Integer uuid) throws TaskNotFoundException, NullTaskException{
        final SubTask subTask = subTasks.get(uuid);
        if (Objects.isNull(subTask) ||  Objects.isNull(uuid)) {
            throw new TaskNotFoundException("SubTask с uuid: " + uuid + " не существует");
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            update(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void deleteSubTask(Integer uuid, Integer epicUuid) throws TaskNotFoundException {
        if(Objects.isNull(uuid) || Objects.isNull(epicUuid)) {
            throw new TaskNotFoundException("Subtask с uuid "+ uuid + " не найден");
        }
        subTasks.remove(uuid);
        historyManager.remove(uuid);
        update(epicUuid, epics.get(epicUuid));
    }

    @Override
    public void update(Integer uuid, SubTask subTask) throws TaskNotFoundException {
        if(Objects.isNull(uuid) || Objects.isNull(subTask)) {
            throw new TaskNotFoundException("Subtask с uuid "+ uuid + " не найден");
        }
        subTasks.put(uuid, subTask);
        update(subTask.getEpicUuid(), epics.get(subTask.getEpicUuid()));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
