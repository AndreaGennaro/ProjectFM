package src.model.elements;

import java.util.ArrayList;

public class User {

    private int userId;
    private String userName;
    private ArrayList<Role> authRoles;
    private Role activeRole;
    private Permission activePerm;

    public User(int userId, String userName, ArrayList<Role> authRoles, Role activeRole, Permission activePerm) {
        this.userId = userId;
        this.userName = userName;
        this.authRoles = authRoles;
        this.activeRole = activeRole;
        this.activePerm = activePerm;
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

    public Role getActiveRole() {
        return activeRole;
    }

    public void setActiveRole(Role activeRole) {
        this.activeRole = activeRole;
    }

    public Permission getActivePerm() {
        return activePerm;
    }

    public void setActivePerm(Permission activePerm) {
        this.activePerm = activePerm;
    }
}
