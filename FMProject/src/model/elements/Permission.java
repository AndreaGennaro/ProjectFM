package src.model.elements;

public class Permission {

    private int id;
    private int object;
    private int operation;

    public Permission(int id, int object, int operation) {
        this.id = id;
        this.object = object;
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObject() {
        return object;
    }

    public void setObject(int object) {
        this.object = object;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }
}
