package test;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.manager.FileBackendTaskManager;
import ru.yandex.practicum.kanban.manager.TaskManager;

import java.io.File;


public abstract class TaskManagerTest<T extends TaskManager> {
    public T manager;
}