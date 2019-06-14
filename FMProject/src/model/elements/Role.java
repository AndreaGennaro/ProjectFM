package src.model.elements;

import java.util.ArrayList;

public class Role {

    private int roleId;
    private String roleName;
    private ArrayList<Permission> permissions;

    public Role(int roleId, String roleName, ArrayList<Permission> permissionList) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.permissions = permissionList;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public ArrayList<Permission> getPermissionList() {
        return permissions;
    }

    public void setPermissionList(ArrayList<Permission> permissionList) {
        this.permissions = permissionList;
    }
}
