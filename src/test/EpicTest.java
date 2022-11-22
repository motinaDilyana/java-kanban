package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.InMemoryTaskManager;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest extends InMemoryTaskManagerTest{

    @Test
    void shouldReturnEpicWithSubtasksAndNewStatusOnCreateEpicWithCorrectInput() {
        Epic epic = manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "NEW", epic.getUuid()));
        manager.update(subtask2.getUuid(), new SubTask(subtask2.getUuid(), subtask2.getName(), subtask2.getDescription(), "NEW", epic.getUuid()));

        Epic savedEpic = manager.getEpicByUuid(epic.getUuid());

        assertEquals(savedEpic.getStatus(), "NEW");
        assertEquals(2, savedEpic.getSubTaskUuids().size());
        assertEquals(savedEpic.getSubTaskUuids().get(0), subtask.getUuid());
    }

    @Test
    void shouldReturnEpicWithSubtasksAndInProgressStatusOnCreateEpicWithCorrectInput() {
        Epic epic = manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "IN_PROGRESS", epic.getUuid()));
        manager.update(subtask2.getUuid(), new SubTask(subtask2.getUuid(), subtask2.getName(), subtask2.getDescription(), "IN_PROGRESS", epic.getUuid()));
        Epic savedEpic = manager.getEpicByUuid(epic.getUuid());

        assertEquals(savedEpic.getStatus(), "IN_PROGRESS");
        assertNotEquals(savedEpic.getStatus(), "NEW");
        assertNotEquals(savedEpic.getStatus(), "DONE");
        assertEquals(2, savedEpic.getSubTaskUuids().size());
        assertEquals(savedEpic.getSubTaskUuids().get(0), subtask.getUuid());
        assertEquals(savedEpic.getSubTaskUuids().get(1), subtask2.getUuid());
    }

    @Test
    void shouldReturnEpicWithSubtasksAndInProgressStatusWhenNewAndDoneSubtasksOnCreateEpicWithCorrectInput() {
        Epic epic = manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "DONE", epic.getUuid()));
        Epic savedEpic = manager.getEpicByUuid(epic.getUuid());

        assertEquals(savedEpic.getStatus(), "IN_PROGRESS");
        assertNotEquals(savedEpic.getStatus(), "NEW");
        assertNotEquals(savedEpic.getStatus(), "DONE");
        assertEquals(2, savedEpic.getSubTaskUuids().size());
        assertEquals(savedEpic.getSubTaskUuids().get(0), subtask.getUuid());
        assertEquals(savedEpic.getSubTaskUuids().get(1), subtask2.getUuid());
    }

    @Test
    void shouldReturnEpicWithSubtasksAndDoneStatusWhenAllSubtasksDoneOnCreateEpicWithCorrectInput() {
        Epic epic = manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "DONE", epic.getUuid()));
        manager.update(subtask2.getUuid(), new SubTask(subtask2.getUuid(), subtask2.getName(), subtask2.getDescription(), "DONE", epic.getUuid()));
        Epic savedEpic = manager.getEpicByUuid(epic.getUuid());

        assertEquals(savedEpic.getStatus(), "DONE");
        assertEquals(2, savedEpic.getSubTaskUuids().size());
        assertEquals(savedEpic.getSubTaskUuids().get(0), subtask.getUuid());
        assertEquals(savedEpic.getSubTaskUuids().get(1), subtask2.getUuid());
    }

    @Test
    void shouldReturnExceptionOnGetByIdEpicWhenInputIdIsIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getEpicByUuid(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.getEpicByUuid(null);
                });

        assertEquals("Epic с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }


    @Test
    void shouldDeleteEmptyEpicOnDeleteEpicWhenInputIsCorrect() {
        Epic epic = manager.createEpic(new Epic("test1", "testD"));
        Epic savedEpic = manager.getEpicByUuid(epic.getUuid());

        Epic epicWithSubtasks = manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        Epic savedEpicWithSubtasks = manager.getEpicByUuid(epicWithSubtasks.getUuid());

        manager.deleteEpic(savedEpic.getUuid());

        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getEpicSubtasks(savedEpicWithSubtasks.getUuid()).size());

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getEpicByUuid(savedEpic.getUuid());
                });
        assertEquals("Epic с uuid " +savedEpic.getUuid() +" не найден", exceptionNotFount.getMessage());
    }

    @Test
    void shouldDeleteEpicWithAllSubtasksOnDeleteEpicWhenInputIsCorrect() {
        Epic epicWithSubtasks = manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        Epic savedEpicWithSubtasks = manager.getEpicByUuid(epicWithSubtasks.getUuid());
        SubTask savedSubtask = manager.getSubTaskByUuid(subtask.getUuid());
        manager.deleteEpic(epicWithSubtasks.getUuid());

        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubTasks().size());

        final TaskNotFoundException exceptionEpicNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getEpicByUuid(savedEpicWithSubtasks.getUuid());
                });
        assertEquals("Epic с uuid " +savedEpicWithSubtasks.getUuid() +" не найден", exceptionEpicNotFount.getMessage());

        final TaskNotFoundException exceptionSubtasksNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getSubTaskByUuid(savedSubtask.getUuid());
                });
        assertEquals("SubTask с uuid: " +savedSubtask.getUuid() +" не существует", exceptionSubtasksNotFount.getMessage());
    }


    @Test
    void shouldReturnExceptionOnDeleteEpicWhenInputIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.deleteEpic(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.deleteEpic(null);
                });

        assertEquals("Epic с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteAllEpicsWithAllSubtasksOnDeleteAllEpics() {
        Epic epicWithSubtasks1 = manager.createEpic(new Epic("test1", "testD"));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks1.getUuid()));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks1.getUuid()));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks1.getUuid()));

        Epic epicWithSubtasks2 = manager.createEpic(new Epic("test1", "testD"));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks2.getUuid()));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks2.getUuid()));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks2.getUuid()));


        Epic savedEpicWithSubtasks = manager.getEpicByUuid(epicWithSubtasks1.getUuid());
        Epic savedEpicWithSubtasks1 = manager.getEpicByUuid(epicWithSubtasks2.getUuid());
        manager.deleteEpic(savedEpicWithSubtasks.getUuid());
        manager.deleteEpic(savedEpicWithSubtasks1.getUuid());


        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubTasks().size());

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.getEpicByUuid(savedEpicWithSubtasks.getUuid());
                });
        assertEquals("Epic с uuid " + savedEpicWithSubtasks.getUuid() + " не найден", exceptionNotFount.getMessage());

    }

    @Test
    void shouldReturnAllEpicsOnGetEpics() {
        Epic epicWithoutSubtask = manager.createEpic(new Epic("test1", "testD"));
        Epic epicWithoutSubtask2 = manager.createEpic(new Epic("test1", "testD"));
        Epic epicWithSubtasks = manager.createEpic(new Epic("test1", "testD"));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        Epic savedEpicWithSubtasks = manager.getEpicByUuid(epicWithSubtasks.getUuid());
        HashMap<Integer, Epic> epics = manager.getEpics();

        assertEquals(3, epics.size());
        assertNotNull(epics);
        assertEquals(epicWithoutSubtask, epics.get(0));
        assertEquals(epicWithoutSubtask2, epics.get(1));
        assertEquals(savedEpicWithSubtasks, epics.get(2));
    }

    @Test
    void shouldReturnUpdatedEpicOnUpdateWhenInputIsCorrect() {
        Epic epicWithoutSubtask = manager.createEpic(new Epic("test1", "testD"));
        manager.update(epicWithoutSubtask.getUuid(),
                new Epic(epicWithoutSubtask.getUuid(), "testNew", "descriptionNew", epicWithoutSubtask.getStatus()));

        Epic updatedEpic = manager.getEpicByUuid(epicWithoutSubtask.getUuid());

        assertNotNull(updatedEpic);
        assertEquals(updatedEpic.getName(), "testNew");
        assertEquals(updatedEpic.getDescription(), "descriptionNew");
        assertEquals(updatedEpic.getStatus(), "NEW");
    }

    @Test
    void shouldReturnExceptionOnUpdateEpicWhenInputUuidIncorrect() {
        Epic epicWithoutSubtask = manager.createEpic(new Epic("test1", "testD"));

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    manager.update(1, epicWithoutSubtask);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    manager.update(null, epicWithoutSubtask);
                });

        assertEquals("Epic с uuid 1 не найден", exceptionNotFount.getMessage());
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
}