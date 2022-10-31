package ru.yandex.practicum.kanban.main;

import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.manager.exceptions.NullTaskException;
import ru.yandex.practicum.kanban.manager.exceptions.TaskNotFoundException;
import ru.yandex.practicum.kanban.task.*;

public class Main {

    public static void main(String[] args) {
        try {
            printTestCases();
        } catch (NullTaskException | TaskNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void printTestCases() {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();

        //Тестирование истории просмотров

        Task task4 = manager.createTask(new Task("name1", "desc1"));
        Task task5 = manager.createTask(new Task("name2", "desc2"));
        //Создаем эпик
        Epic epic6 = manager.createEpic(new Epic("name", "desc"));
        //Создаем подзадачи
        SubTask subTask8 = manager.createSubTask(new SubTask("name1", "desc1", epic6.getUuid()));
        SubTask subTask9 = manager.createSubTask(new SubTask("name1", "desc1", epic6.getUuid()));
        SubTask subTask10 = manager.createSubTask(new SubTask("name1", "desc1", epic6.getUuid()));

        System.out.println("История");
        //Просматриваем созданные таски
        manager.getTaskByUuid(task4.getUuid());
        manager.getTaskByUuid(task5.getUuid());
        manager.getTaskByUuid(task4.getUuid());
        manager.getTaskByUuid(task4.getUuid());

        //Проверяем повторы просмотров тасок
        System.out.println("История тасок: " + manager.getHistory());

        //Дважды смотрим на пустой эпик и один раз на заполненный
        manager.getEpicByUuid(epic6.getUuid());
        manager.getEpicByUuid(epic6.getUuid());

        //Проверяем повторы просмотров эпиков
        System.out.println("История эпиков: " + manager.getHistory());

        //Смотрим по одному разу на сабтаски
        manager.getSubTaskByUuid(subTask8.getUuid());
        manager.getSubTaskByUuid(subTask9.getUuid());
        manager.getSubTaskByUuid(subTask10.getUuid());


        //Проверяем повторы просмотров сабтасков
        System.out.println("История сабтасков: " + manager.getHistory());

        //удаляем задачу
        manager.deleteTask(task4.getUuid());

        //Проверяем историю после удаления
        System.out.println("История после удаления: " + manager.getHistory());

        //удаляем эпик со всеми подзадачами
        manager.deleteEpic(epic6.getUuid());

        //Проверяем историю после удаления эпика
        System.out.println("История после удаления эпика: " + manager.getHistory());
    }
}
