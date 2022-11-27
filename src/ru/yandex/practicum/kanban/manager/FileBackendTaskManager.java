package ru.yandex.practicum.kanban.manager;

import ru.yandex.practicum.kanban.manager.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.task.*;
import ru.yandex.practicum.kanban.task.model.TaskDates;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Integer.parseInt;

public class FileBackendTaskManager extends InMemoryTaskManager {

    private static final String SEPARATOR = ",";
    private final File file;

    public FileBackendTaskManager(File file) {
        this.file = file;
    }
    public FileBackendTaskManager() {
        this.file = new File("");
    }

    protected void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file))) {
            //Формируем шапку файлв
            bufferedWriter.write("id,type,name,status,description,startTime,EndTime,Duration,epic");
            bufferedWriter.newLine();
            //Записываем в файл задачи
            transformTasks(bufferedWriter, tasks.values());
            transformTasks(bufferedWriter, subTasks.values());
            transformTasks(bufferedWriter, epics.values());

            bufferedWriter.newLine();
            //Формируем новую строку с id истории просмотров
            Collection<Task> t = historyManager.getHistory();
            for (Task v : t) {
                bufferedWriter.write(v.getUuid() + ",");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
        }
    }

    public static FileBackendTaskManager loadFromFile(File file) {
        final FileBackendTaskManager manager = new FileBackendTaskManager(file);
        //Начинаем чтение строк из файлв
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                //если строка пустая - идем на следующую и пытаемся прочитать историю просмотров
                if (line.isBlank()) {
                    line = reader.readLine();
                    manager.historyFromString(line);
                    break;
                } else {
                    //иначе создаем задачи из строки
                    manager.fromString(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
        }
        return manager;
    }

    private Epic addEpic(Epic epic) {
        epic = super.createEpic(epic);
        return epic;
    }
    private Task addTask(Task task) {
        task = super.createTask(task);
        super.getTasks();
        return task;
    }
    private SubTask addSubtask(SubTask subTask) {
        subTask = super.createSubTask(subTask);
        return subTask;
    }

    private void historyFromString(String value) {
        if (Objects.nonNull(value) && !value.isEmpty()) {
            for (String number : value.split(SEPARATOR)) {
                Task task = getTaskByUuid(parseInt(number));
                historyManager.add(task);
            }
        }
    }

    private void fromString(String value) {
        //Формируем массив значений для создания Task
        String[] params = value.split(SEPARATOR);

        TaskDates dates = new TaskDates();
        if(!params[5].equals("null") && params[6].equals("null")) {
            dates = new TaskDates(LocalDateTime.parse(params[5]), parseInt(params[6]));
        }
        if (Objects.equals(Type.EPIC.toString(), params[1])) {
            this.addEpic(new Epic(parseInt(params[0]), params[2], params[4], params[3].toUpperCase(), dates));
        } else if (Objects.equals(Type.SUBTASK.toString(), params[1])) {
            this.addSubtask(new SubTask(parseInt(params[0]), params[2], params[4], params[3].toUpperCase(), parseInt(params[8]), dates));
        } else {
            this.addTask(new Task(parseInt(params[0]), params[2], params[4], params[3].toUpperCase(), dates));
        }
    }

    private <T extends Task> void transformTasks(BufferedWriter bufferedWriter, Collection<T> values) throws IOException {
        for (T value : values) {
            bufferedWriter.write(
                    value.getUuid() + ","
                            + value.getType() + ","
                            + value.getName() + ","
                            + value.getStatus() + ","
                            + value.getDescription() + ","
                            + value.getDates()
            );

            //Если Subtask добавляем в конец строки Epic Uuid
            if (value instanceof SubTask) {
                bufferedWriter.write(
                        "," + ((SubTask) value).getEpicUuid()
                );
            }
            bufferedWriter.newLine();
        }
    }


    public Task createTask(Task task) {
        task = super.createTask(task);
        save();
        return task;
    }

    @Override
    public Task getTaskByUuid(Integer uuid) {
        Task task = super.getTaskByUuid(uuid);
        save();
        return task;
    }

    @Override
    public Epic getEpicByUuid(Integer uuid) {
        Epic task = super.getEpicByUuid(uuid);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskByUuid(Integer uuid) {
        SubTask task = super.getSubTaskByUuid(uuid);
        save();
        return task;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void update(Integer uuid, Task task) {
        super.update(uuid, task);
        save();
    }

    @Override
    public void deleteTask(Integer uuid) {
        super.deleteTask(uuid);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteEpic(Integer uuid) {
        super.deleteEpic(uuid);
        save();
    }

    @Override
    public void update(Integer uuid, Epic epic) {
        super.update(uuid, epic);
        save();
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask = super.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteSubTask(Integer uuid, Integer epicUuid) {
        super.deleteSubTask(uuid, epicUuid);
        save();
    }

    @Override
    public void update(Integer uuid, SubTask subTask) {
        super.update(uuid, subTask);
        save();
    }
}
