package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    public Task createTask(Task task);

    public Task getTaskByUuid(Integer uuid);

    public void deleteTasks();

    public void update(Integer uuid, Task task);

    public void deleteTask(Integer uuid);

    public ArrayList<Task> getTasks();

    public Epic createEpic(Epic epic);

    public Epic getEpicByUuid(Integer uuid);

    public void deleteEpics() ;

    public void deleteEpic(Integer uuid);

    public ArrayList<SubTask> getEpicSubtasks(Integer uuid);

    public HashMap<Integer, Epic> getEpics();

    public void update(Integer uuid, Epic epic);

    public ArrayList<SubTask> getSubTasks() ;

    public SubTask createSubTask(SubTask subTask);

    public SubTask getSubTaskByUuid(Integer uuid);

    public void deleteSubTasks() ;

    public void deleteSubTask(Integer uuid, Integer epicUuid);

    public void update(Integer uuid, SubTask subTask);

    public ArrayList<Task> getHistory();
}
