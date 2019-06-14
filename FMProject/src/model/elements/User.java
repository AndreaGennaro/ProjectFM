package src.model.elements;

import java.util.ArrayList;

public class User {

    private int userId;
    private String userName;
    private ArrayList<Role> authRoles;
    private Integer activeRole;

    public User(int userId, String userName, ArrayList<Role> authRoles, Integer activeRole) {
        this.userId = userId;
        this.userName = userName;
        this.authRoles = authRoles;
        this.activeRole = activeRole;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Role> getAuthRoles() {
        return authRoles;
    }

    public void setAuthRoles(ArrayList<Role> authRoles) {
        this.authRoles = authRoles;
    }

    public Integer getActiveRole() {
        return activeRole;
    }

    public void setActiveRole(Integer activeRole) {
        this.activeRole = activeRole;
    }
}
