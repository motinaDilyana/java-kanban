public class SubTask extends Task{

    private Integer uuid;
    private Integer epicUuid;

    public SubTask(String name, String description, Integer epicUuid) {
        super(name, description);
        this.epicUuid = epicUuid;
    }

    public SubTask(String name, String description, String status, Integer epicUuid, Integer uuid) {
        super(name, description, status);
        this.epicUuid = epicUuid;
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "uuid=" + uuid +
                ", epicUuid=" + epicUuid +
                ", status=" + this.getStatus() +
                '}';
    }

    public Integer getEpicUuid() {
        return epicUuid;
    }

    @Override
    public Integer getUuid() {
        return uuid;
    }

}
