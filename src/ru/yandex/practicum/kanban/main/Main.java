package ru.yandex.practicum.kanban.main;

import ru.yandex.practicum.kanban.manager.FileBackendTaskManager;
import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.*;
import ru.yandex.practicum.kanban.task.model.TaskDates;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) {
        try {
            printTestCases();
        } catch (NullTaskException | TaskNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void printTestCases() {

    }
}
