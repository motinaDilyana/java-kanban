package ru.yandex.practicum.kanban.main;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.server.HttpTaskServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskManager httpTaskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.startHttpServer();
    }
}
