package ru.yandex.practicum.kanban.task;

import ru.yandex.practicum.kanban.task.model.TaskDates;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskUuids;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskUuids = new ArrayList<>();
    }

    public Epic(String name, String description, TaskDates dates) {
        super(name, description, dates);
        this.subTaskUuids = new ArrayList<>();
    }

    public Epic(Integer uuid, String name, String description, String status) {
        super(uuid, name, description, status);
        this.subTaskUuids = new ArrayList<>();
    }

    public Epic(Integer uuid, String name, String description, String status, ArrayList<Integer> subTaskUuids) {
        super(uuid, name, description, status);
        this.subTaskUuids = subTaskUuids;
    }
    public Epic(Integer uuid, String name, String description, String status, TaskDates dates) {
        super(uuid, name, description, status, dates);
        this.subTaskUuids = new ArrayList<>();
    }

    public Epic(Integer uuid, String name, String description, String status, ArrayList<Integer> subTaskUuids, TaskDates dates) {
        super(uuid, name, description, status, dates);
        this.subTaskUuids = subTaskUuids;
    }

    public ArrayList<Integer> getSubTaskUuids() {
        return this.subTaskUuids;
    }

    public void setSubTaskUuids(ArrayList<Integer> subTaskUuids) {
        this.subTaskUuids = subTaskUuids;
    }

    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskUuids=" + subTaskUuids +
                "uuid=" + getUuid() +
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
        if (!(o instanceof Epic)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubTaskUuids(), epic.getSubTaskUuids());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubTaskUuids());
    }
}
