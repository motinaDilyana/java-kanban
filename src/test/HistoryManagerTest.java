package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.InMemoryHistoryManager;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private InMemoryHistoryManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldReturnEmptyListOfHistoryTasksWhenNothingAdded() {
        this.taskManager.getHistory();
        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void shouldReturnListOfHistoryTasksWhenInputIsCorrectAndNoDoubles() {
        Task task = new Task(1, "test", "test", "NEW");
        Epic epic = new Epic(2, "test", "test", "NEW");
        SubTask subTask = new SubTask(3, "test", "test","NEW", epic.getUuid());
        this.taskManager.add(task);
        this.taskManager.add(epic);
        this.taskManager.add(subTask);
        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subTask, history.get(2));
    }

    @Test
    void shouldReturnListOfHistoryTasksWhenInputIsCorrectAndExistsDoubles() {
        Task task = new Task(1, "test", "test", "NEW");
        Epic epic = new Epic(2, "test", "test", "NEW");
        SubTask subTask = new SubTask(3, "test", "test","NEW", epic.getUuid());
        this.taskManager.add(task);
        this.taskManager.add(task);
        this.taskManager.add(epic);
        this.taskManager.add(epic);
        this.taskManager.add(subTask);
        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subTask, history.get(2));
    }

    @Test
    void shouldReturnExceptionByAddingToHistoryWhenInputIsNull() {
        final NullTaskException exceptionNotFount = assertThrows(
                NullTaskException.class,
                () -> {
                    this.taskManager.add(null);
                });
        assertEquals("Task не может быть пустым", exceptionNotFount.getMessage());
    }

    @Test
    void shouldReturnListOfHistoryInRemovingFromTheStartOfHistory() {
        Task task = new Task(1, "test", "test", "NEW");
        Task task2 = new Task(2, "test", "test", "NEW");
        Task task3 = new Task(3, "test", "test", "NEW");
        Epic epic1 = new Epic(4, "test", "test", "NEW");
        Epic epic2 = new Epic(6, "test", "test", "NEW");
        SubTask subTask = new SubTask(5, "test", "test","NEW", epic1.getUuid());
        this.taskManager.add(task);
        this.taskManager.add(task2);
        this.taskManager.add(task3);
        this.taskManager.add(epic1);
        this.taskManager.add(epic2);
        this.taskManager.add(subTask);

        this.taskManager.remove(1);
        this.taskManager.remove(2);
        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(4, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(epic1, history.get(1));
        assertEquals(epic2, history.get(2));
        assertEquals(subTask, history.get(3));
    }

    @Test
    void shouldReturnListOfHistoryInRemovingFromTheMiddleOfHistory() {
        Task task = new Task(1, "test", "test", "NEW");
        Task task2 = new Task(2, "test", "test", "NEW");
        Task task3 = new Task(3, "test", "test", "NEW");
        Epic epic1 = new Epic(4, "test", "test", "NEW");
        Epic epic2 = new Epic(6, "test", "test", "NEW");
        SubTask subTask = new SubTask(5, "test", "test","NEW", epic1.getUuid());
        this.taskManager.add(task);
        this.taskManager.add(task2);
        this.taskManager.add(task3);
        this.taskManager.add(epic1);
        this.taskManager.add(epic2);
        this.taskManager.add(subTask);

        this.taskManager.remove(3);
        this.taskManager.remove(4);
        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(4, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(epic2, history.get(2));
        assertEquals(subTask, history.get(3));
    }

    @Test
    void shouldReturnExceptionWhenRemovingIncorrectTask() {
        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.taskManager.remove(null);
                });

        assertEquals("Task не может быть пустым", exceptionNull.getMessage());
    }
}