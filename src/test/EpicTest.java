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

class EpicTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }
    @Test
    void shouldReturnEpicWithSubtasksAndNewStatusOnCreateEpicWithCorrectInput() {
        Epic epic = this.manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        this.manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "NEW", epic.getUuid()));
        this.manager.update(subtask2.getUuid(), new SubTask(subtask2.getUuid(), subtask2.getName(), subtask2.getDescription(), "NEW", epic.getUuid()));

        Epic savedEpic = manager.getEpicByUuid(epic.getUuid());

        assertEquals(savedEpic.getStatus(), "NEW");
        assertEquals(2, savedEpic.getSubTaskUuids().size());
        assertEquals(savedEpic.getSubTaskUuids().get(0), subtask.getUuid());
    }

    @Test
    void shouldReturnEpicWithSubtasksAndInProgressStatusOnCreateEpicWithCorrectInput() {
        Epic epic = this.manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        this.manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "IN_PROGRESS", epic.getUuid()));
        this.manager.update(subtask2.getUuid(), new SubTask(subtask2.getUuid(), subtask2.getName(), subtask2.getDescription(), "IN_PROGRESS", epic.getUuid()));
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
        Epic epic = this.manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        this.manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "DONE", epic.getUuid()));
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
        Epic epic = this.manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        SubTask subtask2 = this.manager.createSubTask(new SubTask("test1", "testD", epic.getUuid()));
        this.manager.update(subtask.getUuid(), new SubTask(subtask.getUuid(), subtask.getName(), subtask.getDescription(), "DONE", epic.getUuid()));
        this.manager.update(subtask2.getUuid(), new SubTask(subtask2.getUuid(), subtask2.getName(), subtask2.getDescription(), "DONE", epic.getUuid()));
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
                    this.manager.getEpicByUuid(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.getEpicByUuid(null);
                });

        assertEquals("Epic с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }


    @Test
    void shouldDeleteEmptyEpicOnDeleteEpicWhenInputIsCorrect() {
        Epic epic = this.manager.createEpic(new Epic("test1", "testD"));
        Epic savedEpic = this.manager.getEpicByUuid(epic.getUuid());

        Epic epicWithSubtasks = this.manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        Epic savedEpicWithSubtasks = this.manager.getEpicByUuid(epicWithSubtasks.getUuid());

        this.manager.deleteEpic(savedEpic.getUuid());

        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getEpicSubtasks(savedEpicWithSubtasks.getUuid()).size());

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.getEpicByUuid(savedEpic.getUuid());
                });
        assertEquals("Epic с uuid " +savedEpic.getUuid() +" не найден", exceptionNotFount.getMessage());
    }

    @Test
    void shouldDeleteEpicWithAllSubtasksOnDeleteEpicWhenInputIsCorrect() {
        Epic epicWithSubtasks = this.manager.createEpic(new Epic("test1", "testD"));
        SubTask subtask = this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        Epic savedEpicWithSubtasks = this.manager.getEpicByUuid(epicWithSubtasks.getUuid());
        SubTask savedSubtask = this.manager.getSubTaskByUuid(subtask.getUuid());
        this.manager.deleteEpic(epicWithSubtasks.getUuid());

        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubTasks().size());

        final TaskNotFoundException exceptionEpicNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.getEpicByUuid(savedEpicWithSubtasks.getUuid());
                });
        assertEquals("Epic с uuid " +savedEpicWithSubtasks.getUuid() +" не найден", exceptionEpicNotFount.getMessage());

        final TaskNotFoundException exceptionSubtasksNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.getSubTaskByUuid(savedSubtask.getUuid());
                });
        assertEquals("SubTask с uuid: " +savedSubtask.getUuid() +" не существует", exceptionSubtasksNotFount.getMessage());
    }


    @Test
    void shouldReturnExceptionOnDeleteEpicWhenInputIncorrect() {
        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.deleteEpic(1);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.deleteEpic(null);
                });

        assertEquals("Epic с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректный ID", exceptionNull.getMessage());
    }

    @Test
    void shouldDeleteAllEpicsWithAllSubtasksOnDeleteAllEpics() {
        Epic epicWithSubtasks1 = this.manager.createEpic(new Epic("test1", "testD"));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks1.getUuid()));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks1.getUuid()));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks1.getUuid()));

        Epic epicWithSubtasks2 = this.manager.createEpic(new Epic("test1", "testD"));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks2.getUuid()));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks2.getUuid()));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks2.getUuid()));


        Epic savedEpicWithSubtasks = this.manager.getEpicByUuid(epicWithSubtasks1.getUuid());
        Epic savedEpicWithSubtasks1 = this.manager.getEpicByUuid(epicWithSubtasks2.getUuid());
        this.manager.deleteEpic(savedEpicWithSubtasks.getUuid());
        this.manager.deleteEpic(savedEpicWithSubtasks1.getUuid());


        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubTasks().size());

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.getEpicByUuid(savedEpicWithSubtasks.getUuid());
                });
        assertEquals("Epic с uuid " + savedEpicWithSubtasks.getUuid() + " не найден", exceptionNotFount.getMessage());

    }

    @Test
    void shouldReturnAllEpicsOnGetEpics() {
        Epic epicWithoutSubtask = this.manager.createEpic(new Epic("test1", "testD"));
        Epic epicWithoutSubtask2 = this.manager.createEpic(new Epic("test1", "testD"));
        Epic epicWithSubtasks = this.manager.createEpic(new Epic("test1", "testD"));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        this.manager.createSubTask(new SubTask("test1", "testD", epicWithSubtasks.getUuid()));
        Epic savedEpicWithSubtasks = this.manager.getEpicByUuid(epicWithSubtasks.getUuid());
        HashMap<Integer, Epic> epics = this.manager.getEpics();

        assertEquals(3, epics.size());
        assertNotNull(epics);
        assertEquals(epicWithoutSubtask, epics.get(0));
        assertEquals(epicWithoutSubtask2, epics.get(1));
        assertEquals(savedEpicWithSubtasks, epics.get(2));
    }

    @Test
    void shouldReturnUpdatedEpicOnUpdateWhenInputIsCorrect() {
        Epic epicWithoutSubtask = this.manager.createEpic(new Epic("test1", "testD"));
        this.manager.update(epicWithoutSubtask.getUuid(),
                new Epic(epicWithoutSubtask.getUuid(), "testNew", "descriptionNew", epicWithoutSubtask.getStatus()));

        Epic updatedEpic = this.manager.getEpicByUuid(epicWithoutSubtask.getUuid());

        assertNotNull(updatedEpic);
        assertEquals(updatedEpic.getName(), "testNew");
        assertEquals(updatedEpic.getDescription(), "descriptionNew");
        assertEquals(updatedEpic.getStatus(), "NEW");
    }

    @Test
    void shouldReturnExceptionOnUpdateEpicWhenInputUuidIncorrect() {
        Epic epicWithoutSubtask = this.manager.createEpic(new Epic("test1", "testD"));

        final TaskNotFoundException exceptionNotFount = assertThrows(
                TaskNotFoundException.class,
                () -> {
                    this.manager.update(1, epicWithoutSubtask);
                });

        final NullTaskException exceptionNull = assertThrows(
                NullTaskException.class,
                () -> {
                    this.manager.update(null, epicWithoutSubtask);
                });

        assertEquals("Epic с uuid 1 не найден", exceptionNotFount.getMessage());
        assertEquals("Некорректные данные", exceptionNull.getMessage());
    }

}