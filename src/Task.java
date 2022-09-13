public class Task {
    private String name;
    private String description;
    private Integer uuid;
    private String status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public Integer getUuid() {
        return this.uuid;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task{name=" + this.name + ", uuid=" + this.uuid + ", status=" + this.status + "}";
    }
}
