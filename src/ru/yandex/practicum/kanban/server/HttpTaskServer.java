package ru.yandex.practicum.kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.Epic;
import ru.yandex.practicum.kanban.task.SubTask;
import ru.yandex.practicum.kanban.task.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer server;
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
        server.stop(120);
    }

    private void methodMapper(HttpExchange httpExchange) throws IOException {
        try {
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    getTasks(httpExchange);
                    break;
                case "POST":
                    createTask(httpExchange);
                    break;
                case "DELETE":
                    deleteTask(httpExchange);
                    break;
                case "PUT":
                    updateTasks(httpExchange);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void getTasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String[] split = path.split("/");

            if (split.length == 2) {
                getAllTasks(split[1], httpExchange);
            } else {
                String query = split[2];

                switch (query) {
                    case "task":
                        try {
                            Optional<Task> optional = Optional.ofNullable(manager.getTaskByUuid(parseId(httpExchange)));
                            find(optional, httpExchange);
                        } finally {
                            httpExchange.close();
                        }
                        break;
                    case "epic":
                        try {
                            int id = parseId(httpExchange);

                            Optional<Epic> optional = Optional.ofNullable(manager.getEpicByUuid(id));
                            find(optional, httpExchange);
                        } finally {
                            httpExchange.close();
                        }
                        break;
                    case "subtask":
                        try {
                            int id = parseId(httpExchange);

                            Optional<SubTask> optional = Optional.ofNullable(manager.getSubTaskByUuid(id));
                            find(optional, httpExchange);
                        } finally {
                            httpExchange.close();
                        }
                        break;
                    case "history":
                        try {
                            ArrayList<Task> history = manager.getHistory();

                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(history.toString().getBytes());
                            }
                        } finally {
                            httpExchange.close();
                        }
                        break;
                    default:
                        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
            }
        } finally {
            httpExchange.close();
        }
    }

    private void getAllTasks(String query, HttpExchange httpExchange) throws IOException {
        switch (query) {
            case "tasks":
                try {
                    List<Task> tasks = manager.getTasks();

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        for (var task : tasks) {
                            os.write(gson.toJson(task).getBytes());
                        }
                    }
                } finally {
                    httpExchange.close();
                }
                break;
            case "epics":
                try {
                    Collection<Epic> epics = manager.getEpics().values();

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        for (var epic : epics) {
                            os.write(gson.toJson(epic).getBytes());
                        }
                    }
                } finally {
                    httpExchange.close();
                }
                break;
            case "subtasks":
                try {
                    Collection<SubTask> subtasks = manager.getSubTasks();

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        for (var sub : subtasks) {
                            os.write(gson.toJson(sub).getBytes());
                        }
                    }
                } finally {
                    httpExchange.close();
                }
                break;
            default:
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        }
    }

    private void createTask(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = path.split("/")[2];

            InputStream inputStream = httpExchange.getRequestBody();
            String inputBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            switch (query) {
                case "task":
                    try {
                        Task task = gson.fromJson(inputBody, Task.class);
                        manager.createTask(task);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                case "epic":
                    try {
                        Epic epic = gson.fromJson(inputBody, Epic.class);
                        manager.createEpic(epic);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                case "subtask":
                    try {
                        SubTask subtask = gson.fromJson(inputBody, SubTask.class);
                        manager.createSubTask(subtask);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void updateTasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = path.split("/")[2];

            InputStream inputStream = httpExchange.getRequestBody();
            String inputBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            switch (query) {
                case "task":
                    try {
                        Task task = gson.fromJson(inputBody, Task.class);
                        manager.update(task.getUuid(), task);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                case "epic":
                    try {
                        Epic epic = gson.fromJson(inputBody, Epic.class);
                        manager.update(epic.getUuid(), epic);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                case "subtask":
                    try {
                        SubTask subtask = gson.fromJson(inputBody, SubTask.class);
                        manager.update(subtask.getUuid(), subtask);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void deleteTask(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = path.split("/")[2];

            switch (query) {
                case "task":
                    try {
                        int id = parseId(httpExchange);
                        manager.deleteTask(id);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                case "epic":
                    try {
                        int id = parseId(httpExchange);
                        manager.deleteEpic(id);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                case "subtask":
                    try {
                        int id = parseId(httpExchange);
                        manager.deleteSubTask(id, 0);

                        httpExchange.sendResponseHeaders(200, 0);
                    } finally {
                        httpExchange.close();
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void find(Optional optional, HttpExchange httpExchange) throws IOException {
        if (optional.isPresent()) {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(optional.get()).getBytes());
            }
        } else {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        }
    }

    private int parseId(HttpExchange httpExchange) throws IOException {
        int id = -1;
        try {
            id = Integer.parseInt(
                    httpExchange
                            .getRequestURI()
                            .getRawQuery()
                            .substring("id=".length()));
        } catch (NumberFormatException formatException) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        }
        return id;
    }
}