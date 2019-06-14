package src.model.elements;

public class Object {

    private int objectId;
    private String objectName;

    public Object(int objectId, String objectName) {
        this.objectId = objectId;
        this.objectName = objectName;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
