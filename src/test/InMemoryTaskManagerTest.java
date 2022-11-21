package test;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }
}