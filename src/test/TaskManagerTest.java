package test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;
import ru.yandex.practicum.kanban.task.model.TaskDates;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    public T manager;

    @Test
    void shouldReturnTaskOnCreateTaskWhenInputIsCorrect() {
        Task task = this.manager.createTask(new Task("test1", "testD"));
        Task savedTask = this.manager.getTaskByUuid(task.getUuid());

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
        assertEquals(1, manager.getTasks().size());
        assertEquals(task, manager.getTasks().get(0));
    }

    @Test
    void shouldReturnExceptionOnCreateTaskWhenInputNull() {
        final NullTaskException exception = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.createTask(null);
                });

        assertEquals("Task не может быть пустым.", exception.getMessage());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void shouldHaveNewStatusOnCreateTaskWithCorrectInput() {
        Task task = this.manager.createTask(new Task("test1", "testD"));
        assertEquals(task.getStatus(), "NEW");
    }


    @Test
    void shouldReturnTaskOnGetByIdTaskWhenInputIsCorrect() {
        Task task = this.manager.createTask(new Task("test1", "testD"));
        Task savedTask = this.manager.getTaskByUuid(task.getUuid());

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
        assertEquals(task, manager.getTasks().get(0));
    }

    @Test
    void shouldReturnExceptionOnGetByIdTaskWhenInputIdIsIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.getTaskByUuid(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.getTaskByUuid(null);
                });

        assertEquals("Task с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void shouldDeleteTaskOnDeleteTaskWhenInputIsCorrect() {
        Task task = this.manager.createTask(new Task("test1", "testD"));
        Task task2 = this.manager.createTask(new Task("test12", "testD2"));
        Task savedTask = manager.getTaskByUuid(task.getUuid());
        this.manager.deleteTask(savedTask.getUuid());

        assertEquals(1, manager.getTasks().size());
        assertEquals(task2, manager.getTaskByUuid(task2.getUuid()));
    }


    @Test
    void shouldReturnExceptionOnDeleteTaskWhenInputIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.deleteTask(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.deleteTask(null);
                });

        assertEquals("Task с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteAllTasksOnDeleteAllTasks() {
        this.manager.createTask(new Task("test1", "testD"));
        this.manager.createTask(new Task("test12", "testD2"));
        this.manager.createTask(new Task("test13", "testD3"));
        this.manager.deleteTasks();

        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void shouldReturnAllTasksOnGetTasks() {
        Task task1 = this.manager.createTask(new Task("test1", "testD"));
        Task task2 = this.manager.createTask(new Task("test12", "testD2"));
        Task task3 = this.manager.createTask(new Task("test13", "testD3"));
        ArrayList<Task> tasks = this.manager.getTasks();

        assertEquals(3, tasks.size());
        assertNotNull(tasks);
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
        assertEquals(task3, tasks.get(2));
    }

    @Test
    void shouldReturnUpdatedTaskOnUpdateWhenInputIsCorrect() {
        Task task1 = this.manager.createTask(new Task("test1", "testD"));
        Integer taskUuid = task1.getUuid();
        TaskDates dates = new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120);
        this.manager.update(task1.getUuid(), new Task(task1.getUuid(), "newTest", "newDescription", "IN_PROGRESS", dates));
        Task updatedTask = manager.getTaskByUuid(taskUuid);

        assertNotNull(updatedTask);
        assertEquals(updatedTask.getName(), "newTest");
        assertEquals(updatedTask.getDescription(), "newDescription");
        assertEquals(updatedTask.getStatus(), "IN_PROGRESS");
        assertEquals(updatedTask.getDates().getDuration(), 120);
        assertEquals(updatedTask.getDates().getStartTime(), LocalDateTime.of(2022,11,22,12,0));
        assertEquals(updatedTask.getDates().getEndTime(), LocalDateTime.of(2022,11,22,14,0));
        assertEquals(updatedTask.getUuid(), taskUuid);
        assertNotEquals(updatedTask, task1);
    }

    @Test
    void shouldReturnExceptionOnUpdateTaskWhenInputUuidIncorrect() {
        Task task1 = this.manager.createTask(new Task("test1", "testD"));
        this.manager.update(task1.getUuid(), new Task(task1.getUuid(), "newTest", "newDescription", "IN_PROGRESS"));

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.update(1, task1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.update(null, task1);
                });

        assertEquals("Task с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректные данные", exceptionNull.getMessage());
    }





    @Test
    void shouldReturnSubTaskOnCreateSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic( "test", "test"));
        SubTask subTask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        Task savedSubTask = this.manager.getSubTaskByUuid(subTask.getUuid());
        ArrayList<SubTask> epicSubTasks = manager.getEpicSubtasks(epic.getUuid());

        assertNotNull(savedSubTask);
        assertEquals(subTask, savedSubTask);
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(subTask, manager.getSubTasks().get(0));
        assertNotNull(epicSubTasks);
        assertEquals(savedSubTask, epicSubTasks.get(0));
    }

    @Test
    void shouldReturnExceptionOnCreateSubTaskWhenInputNull() {
        final NullTaskException exception = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.createSubTask(null);
                });

        assertEquals("SubTask не может быть пустым.", exception.getMessage());
        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    void shouldHaveNewStatusOnCreateSubTaskWithCorrectInput() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        Task subTask = this.manager.createTask(new SubTask("test1", "testD", epic.getUuid()));
        assertEquals(subTask.getStatus(), "NEW");
    }


    @Test
    void shouldReturnSubTaskOnGetByIdSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        Task savedSubTask = this.manager.getSubTaskByUuid(subTask.getUuid());

        assertNotNull(savedSubTask);
        assertEquals(subTask, savedSubTask);
        assertEquals(subTask, manager.getSubTasks().get(0));
    }

    @Test
    void shouldReturnExceptionOnGetByIdSubTaskWhenInputIdIsIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.getSubTaskByUuid(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.getSubTaskByUuid(null);
                });

        assertEquals("SubTask с uuid: 1 не существует", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteSubTaskOnDeleteSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        Epic epic2 = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subTask2 = this.manager.createSubTask(new SubTask("test12", "testD2", epic2.getUuid()));

        SubTask savedSubTask = manager.getSubTaskByUuid(subTask.getUuid());
        this.manager.deleteSubTask(savedSubTask.getUuid(), epic.getUuid());

        assertEquals(1, manager.getSubTasks().size());
        assertEquals(subTask2, manager.getSubTaskByUuid(subTask2.getUuid()));
        assertEquals(subTask2, manager.getEpicSubtasks(epic2.getUuid()).get(0));
        assertNotEquals(0, manager.getEpicSubtasks(epic2.getUuid()).size());
        assertEquals(0, manager.getEpicSubtasks(subTask.getUuid()).size());
    }


    @Test
    void shouldReturnExceptionOnDeleteSubTaskWhenInputIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.deleteSubTask(1, 1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.deleteSubTask(null, null);
                });

        assertEquals("Данные не найдены", exceptionNotFount.getMessage());
        assertEquals("Некорректные ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteAllSubTasksOnDeleteAllSubTasks() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        this.manager.createSubTask(new SubTask("test12", "testD2", epic.getUuid()));
        this.manager.createSubTask(new SubTask("test13", "testD3", epic.getUuid()));
        this.manager.deleteSubTasks();

        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    void shouldReturnAllSubTasksOnGetSubTasks() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subTask2 = this.manager.createSubTask(new SubTask("test12", "testD2", epic.getUuid()));
        SubTask subTask3 = this.manager.createSubTask(new SubTask("test13", "testD3", epic.getUuid()));
        ArrayList<SubTask> Subtasks = this.manager.getSubTasks();

        assertEquals(3, Subtasks.size());
        assertNotNull(Subtasks);
        assertEquals(subTask, Subtasks.get(0));
        assertEquals(subTask2, Subtasks.get(1));
        assertEquals(subTask3, Subtasks.get(2));
    }

    @Test
    void shouldReturnUpdatedSubTaskOnUpdateSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        Integer subTaskUuid = subTask.getUuid();
        TaskDates dates = new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120);
        this.manager.update(subTaskUuid, new SubTask(subTask.getUuid(), "newTest", "newDescription", "IN_PROGRESS", epic.getUuid(), dates));
        Task updatedTask = manager.getSubTaskByUuid(subTaskUuid);

        assertNotNull(updatedTask);
        assertEquals(updatedTask.getName(), "newTest");
        assertEquals(updatedTask.getDescription(), "newDescription");
        assertEquals(updatedTask.getStatus(), "IN_PROGRESS");
        assertEquals(updatedTask.getDates().getDuration(), 120);
        assertEquals(updatedTask.getDates().getStartTime(), LocalDateTime.of(2022,11,22,12,0));
        assertEquals(updatedTask.getDates().getEndTime(), LocalDateTime.of(2022,11,22,14,0));
        assertEquals(updatedTask.getUuid(), subTaskUuid);
    }

    @Test
    void shouldReturnExceptionOnUpdateSubTaskWhenInputUuidIncorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        this.manager.update(subTask.getUuid(), new SubTask(subTask.getUuid(), "newTest", "newDescription", "IN_PROGRESS", epic.getUuid()));

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.update(99, subTask);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.update(null, subTask);
                });

        assertEquals("SubTask с uuid 99 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректные данные", exceptionNull.getMessage());
    }

    @Test
    void shouldReturnEpicWithoutSubtasksAndNewStatusOnCreateEpicWhenInputIsCorrect() {
        Epic epic = this.manager.createEpic(new Epic("test1", "testD"));
        Epic savedEpic = this.manager.getEpicByUuid(epic.getUuid());

        assertNotNull(savedEpic);
        assertEquals(epic, savedEpic);
        assertEquals(1, manager.getEpics().size());
        assertEquals(epic, manager.getEpics().get(0));
        assertEquals(0, savedEpic.getSubTaskUuids().size());
        assertEquals("NEW", savedEpic.getStatus());
    }

    @Test
    void shouldReturnExceptionOnCreateEpicWhenInputNull() {
        final NullTaskException exception = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.createEpic(null);
                });

        assertEquals("Epic не может быть пустым.", exception.getMessage());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void shouldReturnPrioritizedTasksWhenTasksExists() {
        assertEquals(0,manager.getPrioritizedTasks().size());
        Task task1 = manager.createTask(new Task("test", "test", new TaskDates(LocalDateTime.of(2022,12,22,12,0), 120)));
        Task task2 = manager.createTask(new Task("test", "test", new TaskDates(LocalDateTime.of(2022,11,23,12,0), 120)));
        manager.createTask(new Task("test", "test", new TaskDates(LocalDateTime.of(2022,11,24,12,0), 120)));

        Epic epic = manager.createEpic(new Epic("test", "test", new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120)));

        SubTask subTask = manager.createSubTask(new SubTask("test", "test", epic.getUuid(), new TaskDates(LocalDateTime.of(2022,11,23,12,0), 120)));

        //task1 имеет самую позднюю дату, task2 и sabtask имеют одинаковые даты, но uuid task2 меньше
        assertEquals(manager.getPrioritizedTasks().last(), task1);
        assertEquals(manager.getPrioritizedTasks().first(), task2);

        //Изменяем время сабтаска на более раннее чем у task2
        manager.update(subTask.getUuid(), new SubTask(subTask.getUuid(),subTask.getName(),subTask.getDescription(),subTask.getStatus(),subTask.getEpicUuid(),
                new TaskDates(LocalDateTime.of(2022,11,20,12,0), 120)));

        assertEquals(manager.getPrioritizedTasks().last(), task1);
        assertNotEquals(manager.getPrioritizedTasks().first(), task2);
        assertEquals(manager.getPrioritizedTasks().first(), subTask);
    }
}