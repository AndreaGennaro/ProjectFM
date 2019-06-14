package src.model.elements;

public class Operation {

    private int operationId;
    private String name;

    public Operation(int operationId, String name) {
        this.operationId = operationId;
        this.name = name;
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
}
