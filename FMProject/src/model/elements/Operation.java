package src.model.elements;

public class Operation {

    private int operationId;
    private String name;
    private String description;

    public Operation(int operationId, String name, String description) {
        this.operationId = operationId;
        this.name = name;
        this.description = description;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
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
}
