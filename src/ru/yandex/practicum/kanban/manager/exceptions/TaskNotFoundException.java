package ru.yandex.practicum.kanban.manager.exceptions;

public class TaskNotFoundException extends NullPointerException{

    public TaskNotFoundException(String message) {
        super(message);
    }
}
