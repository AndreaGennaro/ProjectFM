package src.model.elements;

public class Permission {

    private int id;
    private RBACObject RBACObject;
    private Operation operation;

    public Permission(int id, RBACObject RBACObject, Operation operation) {
        this.id = id;
        this.RBACObject = RBACObject;
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RBACObject getRBACObject() {
        return RBACObject;
    }

    public void setRBACObject(RBACObject RBACObject) {
        this.RBACObject = RBACObject;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
