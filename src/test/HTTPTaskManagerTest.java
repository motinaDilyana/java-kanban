package test;

import com.google.gson.*;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.manager.HTTPTaskManager;
import ru.yandex.practicum.kanban.server.HttpTaskServer;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;
import ru.yandex.practicum.kanban.task.model.TaskDates;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager>{
    private static Task task;
    private static Task task2;
    private static Epic epic;
    private static Epic epic2;
    private static SubTask subTask;
    private static SubTask subTask2;
    private HttpClient client = HttpClient.newHttpClient();;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static HttpTaskServer taskServer;

    @BeforeAll
    static void setUp() throws IOException {
        HTTPTaskManager manager = new HTTPTaskManager("http://localhost:8085/");
        taskServer = new HttpTaskServer(manager);
        taskServer.startHttpServer();

        initTasks();
        initSubTasksAndEpics();
    }

    private static void initTasks() {
        TaskDates taskDates = new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120);

        task = Task.builder()
                .uuid(0)
                .name("Task1")
                .description("description")
                .status("NEW")
                .dates(taskDates)
                .build();

        task2 = Task.builder()
                .uuid(1)
                .name("Task2")
                .description("description2")
                .status("NEW")
                .dates(taskDates)
                .build();
    }

    private static void initSubTasksAndEpics() {
        TaskDates taskDates = new TaskDates(LocalDateTime.of(2022,11,22,12,0), 120);
        epic = new Epic(2, "test", "ttt", "NEW");
        subTask = new SubTask(3, "test", "ttt", "NEW", epic.getUuid(), taskDates);
        subTask2 = new SubTask(4, "test", "ttt", "IN_PROGRESS", epic.getUuid(), taskDates);
        epic2 = new Epic(2, "test", "ttt", "DONE");
    }


    @Test
    void shouldReturnCreatedTaskWhenInputCorrect() {
        try {
            //Создаем по URL новую задачу
            final HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/task"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnExceptionOnCreateTaskWhenInputNull() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/task"))
                                    .POST(HttpRequest.BodyPublishers.ofString(""))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionOnGetByIdTaskWhenInputIdIsIncorrect() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/task?id=999"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldDeleteTaskOnDeleteTaskWhenInputIsCorrect() {
        try {
            //Создаем по URL новую задачу
            client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/task"))
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            //Запрашиваем созданную задачу по id
            final HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/task?id=0"))
                            .DELETE()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals("", response.body());
            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnExceptionOnDeleteTaskWhenInputIncorrect() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/task?id=999"))
                                    .DELETE()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldReturnAllTasksOnGetTasks() {
        try {
            //Создаем по URL новую задачу
            client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/task"))
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/task"))
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            //Запрашиваем созданную задачу по id
            final HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnCreatedEpicWhenInputCorrect() {
        try {
            //Создаем по URL новую задачу
            final HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/epic"))
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnExceptionOnCreateEpicWhenInputNull() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/epic"))
                                    .POST(HttpRequest.BodyPublishers.ofString(""))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionOnGetByIdEpicWhenInputIdIsIncorrect() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/epic?id=999"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldDeleteEpicOnDeleteWhenInputIsCorrect() {
        try {
            //Создаем по URL новую задачу
            final HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/epic"))
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals("", response.body());
            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnExceptionOnDeleteEpicWhenInputIncorrect() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/epic?id=999"))
                                    .DELETE()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldReturnAllEpicsOnGetEpics() {
        try {
            //Создаем по URL новую задачу
            client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks/epic"))
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            //Запрашиваем созданную задачу по id
            final HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnExceptionOnCreateSubTaskWhenInputNull() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/subtask"))
                                    .POST(HttpRequest.BodyPublishers.ofString(""))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }

    @Test
    void shouldReturnExceptionOnGetByIdSubTaskWhenInputIdIsIncorrect() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/subtask?id=999"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }


    @Test
    void shouldReturnExceptionOnDeleteSubTaskWhenInputIncorrect() {
        final IOException exception = assertThrows(
                IOException.class,
                () -> {
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/tasks/subtask?id=999"))
                                    .DELETE()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );
                });
        assertEquals("HTTP/1.1 header parser received no bytes", exception.getMessage());
    }
}