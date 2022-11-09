package ru.yandex.practicum.kanban.manager.exceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ManagerSaveException extends UncheckedIOException {
    public ManagerSaveException(final String message, IOException cause) {
        super(message, cause);
    }
}
