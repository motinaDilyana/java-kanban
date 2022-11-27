package ru.yandex.practicum.kanban.client;

import ru.yandex.practicum.kanban.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class KVTaskClient {
    private final URI uri;
    private final HttpClient httpClient;
    private String apiToken;
    public KVTaskClient(String url) {
        this.uri = URI.create(url);
        this.httpClient = HttpClient.newHttpClient();

        startKVServer();
        register();
    }

    private void startKVServer() {
        try {
            new KVServer().start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
        }
    }

    private void register() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "register"))
                .build();
        try {
            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            this.apiToken = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.printf("Ошибка регистрации ресурса");
        }
    }

    public void put(String key, String value) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .uri(URI.create(uri + "save/" + key + "?API_TOKEN=" + apiToken))
                .build();
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.printf("Невозможно добавить ресурс");
        }
    }

    public String load(String key) {
        Optional<String> json = Optional.empty();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "load/" + key + "?API_TOKEN=" + apiToken))
                .build();
        try {
            var response = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            json = Optional.ofNullable(response.body());
        } catch (IOException | InterruptedException e) {
            System.out.printf("Ошбка при запросе ресурса");
        }

        return json.orElseThrow(NullPointerException::new);
    }
}
