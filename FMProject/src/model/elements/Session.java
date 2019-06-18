package src.model.elements;

import java.util.ArrayList;

public class Session {

    private ArrayList<User> users;
    private ArrayList<Role> roles;

    public Session(ArrayList<User> users, ArrayList<Role> roles) {
        this.users = users;
        this.roles = roles;
    }

    public ArrayList<Role> getRoles(){
        return roles;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
