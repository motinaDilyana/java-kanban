package test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.InMemoryHistoryManager;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;
import ru.yandex.practicum.kanban.task.model.TaskDates;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest extends InMemoryTaskManagerTest{
    @Test
    void shouldReturnTaskOnCreateTaskWhenInputIsCorrect() {
        Task task = manager.createTask(new Task("test1", "testD"));
        Task savedTask = manager.getTaskByUuid(task.getUuid());

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
                    manager.createTask(null);
                });

        assertEquals("Task не может быть пустым.", exception.getMessage());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void shouldHaveNewStatusOnCreateTaskWithCorrectInput() {
        Task task = manager.createTask(new Task("test1", "testD"));
        assertEquals(task.getStatus(), "NEW");
    }


    @Test
    void shouldReturnTaskOnGetByIdTaskWhenInputIsCorrect() {
        Task task = manager.createTask(new Task("test1", "testD"));
        Task savedTask = manager.getTaskByUuid(task.getUuid());

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
        assertEquals(task, manager.getTasks().get(0));
    }

    @Test
    void shouldReturnExceptionOnGetByIdTaskWhenInputIdIsIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getTaskByUuid(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.getTaskByUuid(null);
                });

        assertEquals("Task с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void shouldDeleteTaskOnDeleteTaskWhenInputIsCorrect() {
        Task task = manager.createTask(new Task("test1", "testD"));
        Task task2 = manager.createTask(new Task("test12", "testD2"));
        Task savedTask = manager.getTaskByUuid(task.getUuid());
        manager.deleteTask(savedTask.getUuid());

        assertEquals(1, manager.getTasks().size());
        assertEquals(task2, manager.getTaskByUuid(task2.getUuid()));
    }


    @Test
    void shouldReturnExceptionOnDeleteTaskWhenInputIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.deleteTask(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.deleteTask(null);
                });

        assertEquals("Task с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteAllTasksOnDeleteAllTasks() {
        manager.createTask(new Task("test1", "testD"));
        manager.createTask(new Task("test12", "testD2"));
        manager.createTask(new Task("test13", "testD3"));
        manager.deleteTasks();

        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void shouldReturnAllTasksOnGetTasks() {
        Task task1 = manager.createTask(new Task("test1", "testD"));
        Task task2 = manager.createTask(new Task("test12", "testD2"));
        Task task3 = manager.createTask(new Task("test13", "testD3"));
        ArrayList<Task> tasks = manager.getTasks();

        assertEquals(3, tasks.size());
        assertNotNull(tasks);
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
        assertEquals(task3, tasks.get(2));
    }

    @Test
    void shouldReturnUpdatedTaskOnUpdateWhenInputIsCorrect() {
        Task task1 = manager.createTask(new Task("test1", "testD"));
        Integer taskUuid = task1.getUuid();
        TaskDates dates = new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120);
        manager.update(task1.getUuid(), new Task(task1.getUuid(), "newTest", "newDescription", "IN_PROGRESS", dates));
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
        Task task1 = manager.createTask(new Task("test1", "testD"));
        manager.update(task1.getUuid(), new Task(task1.getUuid(), "newTest", "newDescription", "IN_PROGRESS"));

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.update(1, task1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.update(null, task1);
                });

        assertEquals("Task с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректные данные", exceptionNull.getMessage());
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