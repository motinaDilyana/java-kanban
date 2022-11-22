package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.manager.FileBackendTaskManager;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackendTaskManagerTest extends TaskManagerTest<FileBackendTaskManager> {
    private File file;

    @BeforeEach
    public void setUp() {
        file = new File("src/ru/yandex/practicum/kanban/tasks.csv");
        manager = new FileBackendTaskManager(file);
    }

    @Test
    void shouldReturnEmptyListOfTasks() throws IOException {
        Task task = manager.createTask(new Task("test", "test"));
        manager.deleteTask(task.getUuid());
        assertTrue( file.exists() );
        assertTrue( file.isFile() );

        String fileContent = Files.readString(Path.of("src/ru/yandex/practicum/kanban/tasks.csv"));
        String[] lines = fileContent.split("\\r?\\n|\\r");
        assertEquals("id,type,name,status,description,startTime,EndTime,Duration,epic", lines[0]);
    }

    @Test
    void shouldReturnAllTypeOfTasks() throws IOException {
        Task task = manager.createTask(new Task("test", "test"));
        Task task2 = manager.createTask(new Task("test", "test"));
        Epic epic = manager.createEpic(new Epic("test", "test"));
        Epic epicWithSub = manager.createEpic(new Epic("test", "test"));
        SubTask subtask = manager.createSubTask(new SubTask("test", "test", epicWithSub.getUuid()));

        assertTrue( file.exists() );
        assertTrue( file.isFile() );
        String fileContent = Files.readString(Path.of("src/ru/yandex/practicum/kanban/tasks.csv"));
        String[] lines = fileContent.split("\\r?\\n|\\r");

        assertEquals("id,type,name,status,description,startTime,EndTime,Duration,epic", lines[0]);
        assertEquals("0,TASK,test,NEW,test,null,null,null", lines[1]);
        assertEquals("1,TASK,test,NEW,test,null,null,null", lines[2]);
        assertEquals("4,SUBTASK,test,NEW,test,null,null,null,3", lines[3]);
        assertEquals("2,EPIC,test,NEW,test,null,null,null", lines[4]);
        assertEquals("3,EPIC,test,NEW,test,null,null,null", lines[5]);
    }

    @Test
    void shouldReturnHistoryString() throws IOException {
        Task task = manager.createTask(new Task("test", "test"));
        Task task2 = manager.createTask(new Task("test", "test"));
        Epic epic = manager.createEpic(new Epic("test", "test"));
        Epic epicWithSub = manager.createEpic(new Epic("test", "test"));
        SubTask subtask = manager.createSubTask(new SubTask("test", "test", epicWithSub.getUuid()));

        manager.getTaskByUuid(task.getUuid());
        manager.getEpicByUuid(epic.getUuid());
        manager.getSubTaskByUuid(subtask.getUuid());

        assertTrue( file.exists() );
        assertTrue( file.isFile() );

        String fileContent = Files.readString(Path.of("src/ru/yandex/practicum/kanban/tasks.csv"));
        String[] lines = fileContent.split("\\r?\\n|\\r");

        assertEquals("id,type,name,status,description,startTime,EndTime,Duration,epic", lines[0]);
        assertEquals("0,TASK,test,NEW,test,null,null,null", lines[1]);
        assertEquals("1,TASK,test,NEW,test,null,null,null", lines[2]);
        assertEquals("4,SUBTASK,test,NEW,test,null,null,null,3", lines[3]);
        assertEquals("2,EPIC,test,NEW,test,null,null,null", lines[4]);
        assertEquals("3,EPIC,test,NEW,test,null,null,null", lines[5]);
        assertEquals("0,2,4,", lines[7]);
    }
}