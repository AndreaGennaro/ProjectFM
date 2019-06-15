package src.model.nusmv;

import src.model.ModelGenerator;
import src.model.elements.*;

import java.util.ArrayList;

public class ModelTranslator {

    private static Session session;
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Role> roles = new ArrayList<>();
    private static ArrayList<RBACObject> objects = new ArrayList<>();
    private static ArrayList<Permission> permissions = new ArrayList<>();
    private static ArrayList<Operation> operations = new ArrayList<>();

    private static String tabSpace = "    ";

    /**
     * The class generate the NuSMV code from the Session object.
     * @return the NuSMV code as a string.
     */
    public static String fromModelToNuSMV(String sessionFilepath){
        // session = ModelGenerator.createSession();

        // Inizializzo le liste di oggetti che rappresentano la sessione
        initializeLists(users, roles, objects, permissions, operations);

        String nusmvCode = "";

        // Scrivo tutti i moduli
        nusmvCode = nusmvCode.concat(generateUserModule());
        nusmvCode = nusmvCode.concat(generateRoleModule());
        nusmvCode = nusmvCode.concat(generateUserRoleModule());
        nusmvCode = nusmvCode.concat(generatePermissionModule());
        nusmvCode = nusmvCode.concat(generateOperationModule());
        nusmvCode = nusmvCode.concat(generateObjectModule());

        //Scrivo il modulo main
        String mainModule = "MODULE main\n";
        mainModule = mainModule + tabSpace + "VAR\n";

        for(User user : users){
            String idStr = String.valueOf(user.getUserId());
            String userActiveOpDomain = "{";

            for(Role role : user.getAuthRoles()){
                for(Permission permission : role.getPermissionList()){
                    String opId = String.valueOf(permission.getId());
                    userActiveOpDomain = userActiveOpDomain.concat(opId + ",");
                }
            }
            userActiveOpDomain = userActiveOpDomain.concat("}");
            userActiveOpDomain = userActiveOpDomain.replace(",}", "}");

            String var = tabSpace + tabSpace + "user" + idStr + " : user(" + idStr + ", " + userActiveOpDomain + ");\n";
            mainModule = mainModule.concat(var);
        }

        for(Role role : roles){
            String idStr = String.valueOf(role.getRoleId());
            String var = tabSpace + tabSpace + "role" + idStr + " : role(" + idStr + ");\n";
            mainModule = mainModule.concat(var);
        }

        for(User user : users){
            for(Role role : user.getAuthRoles()){
                String userIdStr = String.valueOf(user.getUserId());
                String roleIdStr = String.valueOf(role.getRoleId());
                String activeStr = String.valueOf(user.getActiveRole().getRoleId() == role.getRoleId());

                String var = tabSpace + tabSpace + "userRole" + userIdStr + roleIdStr + " : userRole(" +
                        userIdStr + ", " + roleIdStr + ", " + activeStr + ");\n";
                mainModule = mainModule.concat(var);

            }
        }

        for(Permission permission : permissions){
            String idStr = String.valueOf(permission.getId());
            String opId = String.valueOf(permission.getOperation().getOperationId());
            String objId = String.valueOf(permission.getRBACObject().getObjectId());

            String var = tabSpace + tabSpace +
                    "permission" + idStr + " : permission(" + idStr + ", " + opId + ", " + objId + ");\n";
            mainModule = mainModule.concat(var);
        }

        for(RBACObject object : objects){
            String idStr = String.valueOf(object.getObjectId());
            String var = tabSpace + tabSpace + "object" + idStr + " : " + "object(" + idStr + ");\n";
            mainModule = mainModule.concat(var);
        }

        for(Operation operation : operations){
            String idStr = String.valueOf(operation.getOperationId());
            String var = tabSpace + tabSpace + "operation" + idStr + " : " + "operation(" + idStr + ");\n";
            mainModule = mainModule.concat(var);
        }

        return nusmvCode;
    }

    private static void initializeLists(ArrayList<User> users, ArrayList<Role> roles, ArrayList<RBACObject> objects, ArrayList<Permission> permissions, ArrayList<Operation> operations) {
        users = session.getUsers();

        for(User user : users){
            for(Role role : user.getAuthRoles()){
                if(!roles.contains(role)) roles.add(role);
            }
        }

        for(Role role : roles){
            for(Permission permission : role.getPermissionList()){
                if(!permissions.contains(permission)) permissions.add(permission);
            }
        }

        for(Permission permission : permissions){
            if(!objects.contains(permission.getRBACObject())) objects.add(permission.getRBACObject());
            if(!operations.contains(permission.getOperation())) operations.add(permission.getOperation());
        }
    }

    private static String generateModule(String name, String[] variables, String[] domains, String[] assignment, String[] next){
        String module = "MODULE " + name + "(";

        for(int i = 0; i < variables.length; i++){
            String var = variables[i];
            module = module.concat("new" + var);
            if(i < variables.length - 1){
                module = module.concat(", ");
            }
        }

        module = module.concat(")\n");
        module = module.concat(tabSpace + "VAR\n");
        for(int i = 0; i < variables.length; i++){
            module = module.concat(tabSpace + tabSpace + variables[i] + " : " + domains[i] + ";\n");
        }

        module = module.concat(tabSpace + "ASSIGN\n");
        for (int i = 0; i < variables.length; i++) {
            String variable = variables[i];
            module = module.concat(tabSpace + tabSpace + "init(" + variable + ") := new" + assignment[i] + ";\n");
            module = module.concat(tabSpace + tabSpace + "next(" + variable + ") := " + next[i] + ";\n");
        }
        module = module.concat("\n");
        return module;
    }

    private static String generateUserModule(){
        return generateModule("user",
                new String[]{"id", "activeOp"},
                new String[]{"0..100", "0.." + operations.size()},
                new String[]{"id", "activeOp"},
                new String[]{"id", "activeOp"});
    }

    private static String generateRoleModule(){
        return generateModule(
                "role",
                new String[]{"id"},
                new String[]{"0..100"},
                new String[]{"id"},
                new String[]{"id"});
    }

    private static String generateUserRoleModule(){
        return generateModule("userRole",
                new String[]{"user", "role", "active"},
                new String[]{"0..100", "0..100", "boolean"},
                new String[]{"user", "role", "active"},
                new String[]{"user", "role", "active"});
    }

    private static String generatePermissionModule(){
        return generateModule(
                "permission",
                new String[]{"permid", "operid", "objectid"},
                new String[]{"0..100", "0..100", "0..100"},
                new String[]{"id", "id", "id"},
                new String[]{"id", "id", "id"});
    }

    private static String generateObjectModule(){
        return generateModule(
                "object",
                new String[]{"id"},
                new String[]{"0..100"},
                new String[]{"id"},
                new String[]{"id"});
    }

    private static String generateOperationModule(){
        return generateModule(
                "operation",
                new String[]{"id"},
                new String[]{"0..100"},
                new String[]{"id"},
                new String[]{"id"});
    }
}
