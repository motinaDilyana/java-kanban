package ru.yandex.practicum.kanban.manager.exceptions;

public class NullTaskException extends NullPointerException{


    public NullTaskException(final String message) {
        super(message);
    }
}
