package ru.yandex.practicum.kanban.main;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;

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
