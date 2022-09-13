import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskUuids;
    private Integer uuid;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskUuids = new ArrayList<>();
    }

    public Epic(String name, String description, String status, Integer uuid) {
        super(name, description, status);
        this.subTaskUuids = new ArrayList<>();
        this.uuid = uuid;
    }

    public Epic(String name, String description, String status, Integer uuid, ArrayList<Integer> subTaskIds) {
        super(name, description, status);
        this.subTaskUuids = subTaskIds;
        this.uuid = uuid;
    }

    public ArrayList<Integer> getSubTaskUuids() {
        return this.subTaskUuids;
    }

    @Override
    public Integer getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskUuids=" + subTaskUuids +
                ", uuid=" + uuid + ", status="+ getStatus() +
                '}';
    }
}
