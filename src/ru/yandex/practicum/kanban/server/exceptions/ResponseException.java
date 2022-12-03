package ru.yandex.practicum.kanban.server.exceptions;

import java.io.IOException;

public class ResponseException extends IOException {
    public ResponseException(final String message) {
        super(message);
    }
}
