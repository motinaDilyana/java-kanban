public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();
        //Создаем две задачи
        Task task1 = manager.createTask(new Task("name1", "desc1"));
        Task task2 = manager.createTask(new Task("name2", "desc2"));
        //Создаем пустой эпик
        Epic epic1 = manager.createEpic(new Epic("name", "desc"));
        Epic epic2 = manager.createEpic(new Epic("name2", "desc2"));
        //Создаем подзадачи
        SubTask subTask1 = manager.createSubTask(new SubTask("name1", "desc1", epic1.getUuid()));
        SubTask subTask2 = manager.createSubTask(new SubTask("name1", "desc1", epic1.getUuid()));
        SubTask subTask3 = manager.createSubTask(new SubTask("name1", "desc1", epic2.getUuid()));

        //Выводим созданные задачи
        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());

        manager.update(subTask1.getUuid(), new SubTask("nameUpd", "descUpd", "NEW", subTask1.getEpicUuid(), subTask1.getUuid()));
        manager.update(subTask2.getUuid(), new SubTask("nameUpd2", "descUpd2", "DONE", subTask2.getEpicUuid(), subTask2.getUuid()));
        manager.update(subTask3.getUuid(), new SubTask("nameUpd3", "descUpd3", "DONE", subTask3.getEpicUuid(), subTask3.getUuid()));
        manager.update(task1.getUuid(), new Task(task1.getUuid(), "nameUpd3", "descUpd3", "DONE"));

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());

        manager.deleteSubTask(subTask1.getUuid(), subTask1.getEpicUuid());
        manager.deleteEpic(epic2.getUuid());

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
    }
}
