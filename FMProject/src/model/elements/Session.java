package src.model.elements;

import java.util.ArrayList;

public class Session {

    private ArrayList<User> users;

    public Session(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
