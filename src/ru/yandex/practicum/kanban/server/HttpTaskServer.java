package ru.yandex.practicum.kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.server.exceptions.ResponseException;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;
    private static final int SERVER_DELAY = 120;
    private static final int RESPONSE_LENGTH = 0;

    private static final int URL_PREFIX_POSITION = 2;
    private static final int URL_PREFIX_TASKS_LIST_POSITION = 1;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = manager;
        this.gson = Managers.getGson();
    }

    public void startHttpServer() {
        server.createContext("/tasks/task", this::methodMapper);
        server.createContext("/tasks/subtask", this::methodMapper);
        server.createContext("/tasks/epic", this::methodMapper);
        server.createContext("/tasks", this::methodMapper);
        server.createContext("/tasks/history", this::methodMapper);
        server.start();
    }

    public void stopHttpServer() {
        server.stop(SERVER_DELAY);
    }

    private void methodMapper(HttpExchange httpExchange) throws ResponseException {
        String path = httpExchange.getRequestURI().getPath();
        String[] url = path.split("/");
        String urlType;
        if(url.length == 3) {
            urlType = path.split("/")[URL_PREFIX_POSITION];
        } else {
            urlType = path.split("/")[URL_PREFIX_TASKS_LIST_POSITION];
        }

        String query = httpExchange.getRequestURI().getQuery();

        Integer id = null;
        try {
            if (Objects.nonNull(query)) {
                id = parseId(httpExchange);
            }

            InputStream inputStream = httpExchange.getRequestBody();
            String inputBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    getTasks(httpExchange, urlType, id);
                    break;
                case "POST":
                    createTask(httpExchange, urlType, inputBody);
                    break;
                case "DELETE":
                    deleteTask(httpExchange, urlType, id);
                    break;
                case "PUT":
                    updateTasks(httpExchange, urlType, inputBody);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, RESPONSE_LENGTH);
            }
        } catch (IOException e) {
            throw new ResponseException("Could not handle request");
        } finally {
            httpExchange.close();
        }

    }

    private <T extends Task> void getTasks(HttpExchange httpExchange, String url, Integer id) throws IOException {
        try {
            Collection optional;
            if (Objects.isNull(id)) {
                getAllTasks(url, httpExchange);
            } else {
                switch (url) {
                    case "task":
                        optional = Collections.singletonList(manager.getTaskByUuid(id));
                        sendResponse(optional, httpExchange);
                        httpExchange.close();
                        break;
                    case "epic":
                        optional = Collections.singletonList(manager.getEpicByUuid(id));
                        sendResponse(optional, httpExchange);
                        httpExchange.close();
                        break;
                    case "subtask":
                        optional = Collections.singletonList(manager.getSubTaskByUuid(id));
                        sendResponse(optional, httpExchange);
                        httpExchange.close();
                        break;
                    case "history":
                        optional = Collections.singletonList(manager.getHistory());
                        sendResponse(optional, httpExchange);
                        httpExchange.close();
                        break;
                }
            }
        } finally {
            httpExchange.close();
        }
    }

    private void getAllTasks(String query, HttpExchange httpExchange) {
        try {
            switch (query) {
                case "tasks":
                    List<Task> tasks = manager.getTasks();
                    sendResponse(tasks, httpExchange);
                    httpExchange.close();
                    break;
                case "epics":
                    Collection<Epic> epics = manager.getEpics().values();
                    sendResponse(epics, httpExchange);
                    httpExchange.close();
                    break;
                case "subtasks":
                    Collection<SubTask> subtasks = manager.getSubTasks();
                    sendResponse(subtasks, httpExchange);
                    httpExchange.close();
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, RESPONSE_LENGTH);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not send response from get all tasks");
        } finally {
            httpExchange.close();
        }
    }

    private void createTask(HttpExchange httpExchange, String query, String inputBody) throws IOException {
        try {
            switch (query) {
                case "task":
                    Task task = gson.fromJson(inputBody, Task.class);
                    manager.createTask(task);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    httpExchange.close();
                    break;
                case "epic":
                    Epic epic = gson.fromJson(inputBody, Epic.class);
                    manager.createEpic(epic);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    httpExchange.close();
                    break;
                case "subtask":
                    SubTask subtask = gson.fromJson(inputBody, SubTask.class);
                    manager.createSubTask(subtask);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    httpExchange.close();
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, RESPONSE_LENGTH);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void updateTasks(HttpExchange httpExchange, String query, String inputBody) throws IOException {
        try {
            switch (query) {
                case "task":
                    Task task = gson.fromJson(inputBody, Task.class);
                    manager.update(task.getUuid(), task);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    break;
                case "epic":
                    Epic epic = gson.fromJson(inputBody, Epic.class);
                    manager.update(epic.getUuid(), epic);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    break;
                case "subtask":
                    SubTask subtask = gson.fromJson(inputBody, SubTask.class);
                    manager.update(subtask.getUuid(), subtask);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, RESPONSE_LENGTH);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void deleteTask(HttpExchange httpExchange, String query, int id) throws IOException {
        try {
            switch (query) {
                case "task":
                    manager.deleteTask(id);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    break;
                case "epic":
                    manager.deleteEpic(id);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    break;
                case "subtask":
                    manager.deleteSubTask(id, 0);
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, RESPONSE_LENGTH);
            }
        } finally {
            httpExchange.close();
        }
    }

    private <T extends Task> void sendResponse(Collection<T> optional, HttpExchange httpExchange) throws IOException {

            if (!optional.isEmpty()) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, RESPONSE_LENGTH);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    for (T value : optional) {
                        os.write(gson.toJson(value).getBytes());
                    }
                }
            } else {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, RESPONSE_LENGTH);
            }
    }

    private int parseId(HttpExchange httpExchange) throws IOException {
        int id = -1;
        id = Integer.parseInt(
                httpExchange
                        .getRequestURI()
                        .getRawQuery()
                        .substring("id=".length()));

        return id;
    }
}