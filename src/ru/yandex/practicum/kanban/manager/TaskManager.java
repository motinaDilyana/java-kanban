package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

     Task createTask(Task task);

     Task getTaskByUuid(Integer uuid);

     void deleteTasks();

     void update(Integer uuid, Task task);

     void deleteTask(Integer uuid);

     ArrayList<Task> getTasks();

     Epic createEpic(Epic epic);

     Epic getEpicByUuid(Integer uuid);

     void deleteEpics();

     void deleteEpic(Integer uuid);

     ArrayList<SubTask> getEpicSubtasks(Integer uuid);

     HashMap<Integer, Epic> getEpics();

     void update(Integer uuid, Epic epic);

     ArrayList<SubTask> getSubTasks();

     SubTask createSubTask(SubTask subTask);

     SubTask getSubTaskByUuid(Integer uuid);

     void deleteSubTasks();

     void deleteSubTask(Integer uuid, Integer epicUuid);

     void update(Integer uuid, SubTask subTask);

     ArrayList<Task> getHistory();
}
