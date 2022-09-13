import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    Integer uuid = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    
    public Task createTask(Task task) {
        task = new Task(uuid++, task.getName(), task.getDescription(), Statuses.NEW.toString());
        tasks.put(task.getUuid(), task);
        return task;
    }
    
    public Task getTaskByUuid(Integer uuid) {
        return tasks.get(uuid);
    }
    
    public void deleteTasks() {
        tasks.clear();
    }
    
    public Task update(Integer uuid, Task task) {
        tasks.put(uuid, task);
        return tasks.get(uuid);
    }
    
    public void deleteTask(Integer uuid) {
        tasks.remove(uuid);
    }
    
    public HashMap<Integer, Task> getTasks() {
        return this.tasks;
    }

    public Epic createEpic(Epic epic) {
        epic = new Epic(epic.getName(), epic.getDescription(), Statuses.NEW.toString(), uuid++);
        epics.put(epic.getUuid(), epic);
        return epic;
    }
    
    public Epic getEpicByUuid(Integer uuid) {
        return epics.get(uuid);
    }
    
    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }
    
    public void deleteEpic(Integer uuid) {
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicUuid().equals(uuid)) {
                subTasks.remove(entry.getKey());
            }
        }
        epics.remove(uuid);
    }
    
    public ArrayList<SubTask> getEpicSubtasks(Integer uuid) {
        ArrayList<SubTask> result = new ArrayList<>();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if (entry.getValue().getEpicUuid().equals(uuid)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }
    
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }
    
    private String updateStatus(Epic epic) {
        ArrayList<Integer> epicSubTaskUuids = epic.getSubTaskUuids().isEmpty() ? new ArrayList<>() : epic.getSubTaskUuids();
        Integer doneCounter = 0;
        Integer newCounter = 0;

        String epicStatus = Statuses.NEW.toString();

        if(!epicSubTaskUuids.isEmpty()) {
            for (Object o : epicSubTaskUuids) {
                if (subTasks.containsKey(o)) {
                    SubTask task = subTasks.get(o);
                    if (task.getStatus().equals(Statuses.NEW.toString())) {
                        newCounter++;
                    } else if (task.getStatus().equals(Statuses.DONE.toString())) {
                        doneCounter++;
                    }
                }
            }

            if(doneCounter == 0) {
                epicStatus = Statuses.NEW.toString();
            } else if (newCounter == 0) {
                epicStatus = Statuses.DONE.toString();
            } else {
                epicStatus = Statuses.IN_PROGRESS.toString();
            }
        }
        return  epicStatus;
    }


    public Epic update(Integer uuid, Epic epic) {
        ArrayList<Integer> subTasksUuids = new ArrayList<>();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            if(entry.getValue().getEpicUuid().equals(uuid)) {
                subTasksUuids.add(entry.getKey());
            }
        }

        String epicStatus = updateStatus(epic);
        epics.put(uuid, new Epic(epic.getName(), epic.getDescription(), epicStatus, uuid, subTasksUuids));
        return epics.get(uuid);
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return this.subTasks;
    }
    
    public SubTask createSubTask(SubTask subTask) {
        subTask = new SubTask(subTask.getName(), subTask.getDescription(), Statuses.NEW.toString(), subTask.getEpicUuid(), uuid++);
        subTasks.put(subTask.getUuid(), subTask);
        update(subTask.getEpicUuid(), epics.get(subTask.getEpicUuid()));
        return subTask;
    }
    
    public SubTask getSubTaskByUuid(Integer uuid) {
        return subTasks.get(uuid);
    }
    
    public void deleteSubTasks() {
        subTasks.clear();
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            update(entry.getKey(), entry.getValue());
        }
    }
    
    public void deleteSubTask(Integer uuid, Integer epicUuid) {
        subTasks.remove(uuid);
        update(epicUuid, epics.get(epicUuid));
    }
    
    public SubTask update(Integer uuid, SubTask subTask) {
        subTasks.put(uuid, subTask);
        update(subTask.getEpicUuid(), epics.get(subTask.getEpicUuid()));
        return subTasks.get(uuid);
    }
}
