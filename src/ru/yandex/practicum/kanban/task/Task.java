package ru.yandex.practicum.kanban.task;

import java.util.Objects;

public class Task {
    private Integer uuid;
    private String name;
    private String description;
    private String status;
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(Integer uuid, String name, String description, String status) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUuid() {
        return this.uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{name=" + this.name + ", uuid=" + this.uuid + ", status=" + this.status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(getUuid(), task.getUuid())
                && Objects.equals(getName(), task.getName())
                && Objects.equals(getDescription(), task.getDescription())
                && Objects.equals(getStatus(), task.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getName(), getDescription(), getStatus());
    }
}
