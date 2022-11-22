package test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;
import ru.yandex.practicum.kanban.task.model.TaskDates;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest extends InMemoryTaskManagerTest{
    @Test
    void shouldReturnSubTaskOnCreateSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic( "test", "test"));
        SubTask subTask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        Task savedSubTask = manager.getSubTaskByUuid(subTask.getUuid());
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
                    manager.createSubTask(null);
                });

        assertEquals("SubTask не может быть пустым.", exception.getMessage());
        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    void shouldHaveNewStatusOnCreateSubTaskWithCorrectInput() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        Task subTask = manager.createTask(new SubTask("test1", "testD", epic.getUuid()));
        assertEquals(subTask.getStatus(), "NEW");
    }


    @Test
    void shouldReturnSubTaskOnGetByIdSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        Task savedSubTask = manager.getSubTaskByUuid(subTask.getUuid());

        assertNotNull(savedSubTask);
        assertEquals(subTask, savedSubTask);
        assertEquals(subTask, manager.getSubTasks().get(0));
    }

    @Test
    void shouldReturnExceptionOnGetByIdSubTaskWhenInputIdIsIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getSubTaskByUuid(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.getSubTaskByUuid(null);
                });

        assertEquals("SubTask с uuid: 1 не существует", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteSubTaskOnDeleteSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        Epic epic2 = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subTask2 = manager.createSubTask(new SubTask("test12", "testD2", epic2.getUuid()));

        SubTask savedSubTask = manager.getSubTaskByUuid(subTask.getUuid());
        manager.deleteSubTask(savedSubTask.getUuid(), epic.getUuid());

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
                    manager.deleteSubTask(1, 1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.deleteSubTask(null, null);
                });

        assertEquals("Данные не найдены", exceptionNotFount.getMessage());
        assertEquals("Некорректные ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteAllSubTasksOnDeleteAllSubTasks() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        manager.createSubTask(new SubTask("test12", "testD2", epic.getUuid()));
        manager.createSubTask(new SubTask("test13", "testD3", epic.getUuid()));
        manager.deleteSubTasks();

        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    void shouldReturnAllSubTasksOnGetSubTasks() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subTask2 = manager.createSubTask(new SubTask("test12", "testD2", epic.getUuid()));
        SubTask subTask3 = manager.createSubTask(new SubTask("test13", "testD3", epic.getUuid()));
        ArrayList<SubTask> Subtasks = manager.getSubTasks();

        assertEquals(3, Subtasks.size());
        assertNotNull(Subtasks);
        assertEquals(subTask, Subtasks.get(0));
        assertEquals(subTask2, Subtasks.get(1));
        assertEquals(subTask3, Subtasks.get(2));
    }

    @Test
    void shouldReturnUpdatedSubTaskOnUpdateSubTaskWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test", "test"));
        SubTask subTask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        Integer subTaskUuid = subTask.getUuid();
        TaskDates dates = new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120);
        manager.update(subTaskUuid, new SubTask(subTask.getUuid(), "newTest", "newDescription", "IN_PROGRESS", epic.getUuid(), dates));
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
        SubTask subTask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        manager.update(subTask.getUuid(), new SubTask(subTask.getUuid(), "newTest", "newDescription", "IN_PROGRESS", epic.getUuid()));

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.update(99, subTask);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.update(null, subTask);
                });

        assertEquals("SubTask с uuid 99 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректные данные", exceptionNull.getMessage());
    }
}