package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.client.KVTaskClient;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;
import com.google.gson.Gson;


public class HTTPTaskManager extends FileBackendTaskManager{
    private KVTaskClient kvTaskClient;
    private Gson gson;

    public HTTPTaskManager(String urlToServer) {
        this.kvTaskClient = new KVTaskClient(urlToServer);
        gson = Managers.getGson();
    }

    @Override
    protected void save() {
        super.getTasks().forEach(
                task -> kvTaskClient.put(
                        String.valueOf(task.getUuid()),
                        gson.toJson(task)));

        super.getEpics().forEach(
                (key, task) -> kvTaskClient.put(
                        String.valueOf(task.getUuid()),
                        gson.toJson(task)));

        super.getSubTasks().forEach(
                task -> kvTaskClient.put(
                        String.valueOf(task.getUuid()),
                        gson.toJson(task)));
    }

    public Task createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public Task getTaskByUuid(Integer uuid) {
        return super.getTaskByUuid(uuid);
    }

    @Override
    public Epic getEpicByUuid(Integer uuid) {
        return super.getEpicByUuid(uuid);
    }

    @Override
    public SubTask getSubTaskByUuid(Integer uuid) {
        return super.getSubTaskByUuid(uuid);
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
    }

    @Override
    public void update(Integer uuid, Task task) {
        super.update(uuid, task);
        
    }

    @Override
    public void deleteTask(Integer uuid) {
        super.deleteTask(uuid);
        
    }

    @Override
    public Epic createEpic(Epic epic) {
        return super.createEpic(epic);
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        
    }

    @Override
    public void deleteEpic(Integer uuid) {
        super.deleteEpic(uuid);
        
    }

    @Override
    public void update(Integer uuid, Epic epic) {
        super.update(uuid, epic);
        
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        return super.createSubTask(subTask);
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        
    }

    @Override
    public void deleteSubTask(Integer uuid, Integer epicUuid) {
        super.deleteSubTask(uuid, epicUuid);
        
    }

    @Override
    public void update(Integer uuid, SubTask subTask) {
        super.update(uuid, subTask);
        
    }
}
