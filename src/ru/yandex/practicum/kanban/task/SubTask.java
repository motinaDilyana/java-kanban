package ru.yandex.practicum.kanban.task;

import ru.yandex.practicum.kanban.task.model.TaskDates;
import java.util.Objects;

public class SubTask extends Task {
    private final Integer epicUuid;
    public SubTask(String name, String description, Integer epicUuid) {
        super(name, description);
        this.epicUuid = epicUuid;
    }

    public SubTask(String name, String description, Integer epicUuid, TaskDates dates) {
        super(name, description, dates);
        this.epicUuid = epicUuid;
    }

    public SubTask(Integer uuid, String name, String description, String status, Integer epicUuid) {
        super(uuid, name, description, status);
        this.epicUuid = epicUuid;
    }

    public SubTask(Integer uuid, String name, String description, String status, Integer epicUuid, TaskDates dates) {
        super(uuid, name, description, status, dates);
        this.epicUuid = epicUuid;
    }

    public Integer getEpicUuid() {
        return epicUuid;
    }

    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "uuid=" + getUuid() +
                ", epicUuid=" + epicUuid +
                ", status=" + this.getStatus() +
                ", type=" + this.getType() +
                ", startTime=" + this.getDates().getStartTime() +
                ", endTime=" + this.getDates().getEndTime() +
                ", duration=" + this.getDates().getDuration() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(getEpicUuid(), subTask.getEpicUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEpicUuid());
    }
}
