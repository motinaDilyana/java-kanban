package ru.yandex.practicum.kanban.task.model;

import java.time.LocalDateTime;
import java.util.Objects;
public class TaskDates {
    private LocalDateTime startTime;
    private Integer duration;

    public TaskDates() {}
    public TaskDates(LocalDateTime startTime, Integer duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return Objects.nonNull(startTime) ? this.startTime.plusMinutes(duration) : null;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return
                "" + startTime +
                "," + getEndTime() +
                "," + duration ;
    }
}
