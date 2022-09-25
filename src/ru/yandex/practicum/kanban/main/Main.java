package ru.yandex.practicum.kanban.main;

import ru.yandex.practicum.kanban.manager.Managers;
import ru.yandex.practicum.kanban.manager.TaskManager;
import ru.yandex.practicum.kanban.task.*;

public class Main {

    public static void main(String[] args) {
        printTestCases();
    }

    private static void printTestCases() {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();

        //Тестирование истории просмотров

        Task task4 = manager.createTask(new Task("name1", "desc1"));
        Task task5 = manager.createTask(new Task("name2", "desc2"));
        //Создаем пустой эпик
        Epic epic6 = manager.createEpic(new Epic("name", "desc"));
        Epic epic7 = manager.createEpic(new Epic("name2", "desc2"));
        //Создаем подзадачи
        SubTask subTask8 = manager.createSubTask(new SubTask("name1", "desc1", epic6.getUuid()));
        SubTask subTask9 = manager.createSubTask(new SubTask("name1", "desc1", epic6.getUuid()));
        SubTask subTask10 = manager.createSubTask(new SubTask("name1", "desc1", epic7.getUuid()));

        System.out.println("История");
        //Просматриваем созданные таски

        manager.getTaskByUuid(task4.getUuid());
        manager.getTaskByUuid(task4.getUuid());
        manager.getTaskByUuid(task5.getUuid());

        manager.getEpicByUuid(epic7.getUuid());
        manager.getEpicByUuid(epic7.getUuid());
        manager.getEpicByUuid(epic7.getUuid());
        manager.getEpicByUuid(epic6.getUuid());

        manager.getSubTaskByUuid(subTask8.getUuid());
        manager.getSubTaskByUuid(subTask9.getUuid());
        manager.getSubTaskByUuid(subTask10.getUuid());
        manager.getSubTaskByUuid(subTask8.getUuid());

        System.out.println(manager.getHistory());
    }
}
