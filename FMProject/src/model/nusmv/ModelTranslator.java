package src.model.nusmv;

//import src.model.ModelGenerator;
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
        try {
            session = ModelGenerator.createSession(sessionFilepath);


            // Inizializzo le liste di oggetti che rappresentano la sessione
            initializeLists();

            String nusmvCode = "";

            // Scrivo tutti i moduli
            nusmvCode = nusmvCode.concat(generateUserModule());
            nusmvCode = nusmvCode.concat(generateRoleModule());
            nusmvCode = nusmvCode.concat(generateUserRoleModule());
            nusmvCode = nusmvCode.concat(generatePermissionModule());
            nusmvCode = nusmvCode.concat(generateRolePermissionModule());
            nusmvCode = nusmvCode.concat(generateOperationModule());
            nusmvCode = nusmvCode.concat(generateObjectModule());

            //Scrivo il modulo main
            String mainModule = "MODULE main\n";
            mainModule = mainModule + tabSpace + "VAR\n";


            // Writes all users instantiations
            for (User user : users) {
                String idStr = String.valueOf(user.getUserId());
                String userActivePermDomain = "{";

                for (Role role : user.getAuthRoles()) {
                    for (Permission permission : role.getPermissionList()) {
                        String permId = String.valueOf(permission.getId());
                        userActivePermDomain = userActivePermDomain.concat(permId + ",");
                    }
                }
                userActivePermDomain = userActivePermDomain.concat("}");
                userActivePermDomain = userActivePermDomain.replace(",}", "}");

                String var = tabSpace + tabSpace + "user" + idStr + " : user(" + idStr + ", " + userActivePermDomain + ");" +
                        " -- user " + user.getUserName() + "\n";
                mainModule = mainModule.concat(var);
            }

            // Writes roles instantiations
            for (Role role : roles) {
                String idStr = String.valueOf(role.getRoleId());
                String var = tabSpace + tabSpace + "role" + idStr + " : role(" + idStr + "); -- role " + role.getRoleName() + "\n";
                mainModule = mainModule.concat(var);
            }

            // Writes user-roles intantiations
            for (User user : users) {
                String userIdStr = String.valueOf(user.getUserId());
                for (Role role : user.getAuthRoles()) {
                    String roleIdStr = String.valueOf(role.getRoleId());
                    String activeStr = String.valueOf(user.getActiveRole().getRoleId() == role.getRoleId()).toUpperCase();

                    String var = tabSpace + tabSpace + "ur" + userIdStr + "_" + roleIdStr + " : userRole(" +
                            userIdStr + ", " + roleIdStr + ", " + activeStr + ", Aut); " +
                            "-- (" + user.getUserName() + ", " + role.getRoleName() + ")\n";
                    mainModule = mainModule.concat(var);

                }
            }

            for (Permission permission : permissions) {
                String idStr = String.valueOf(permission.getId());
                String opId = String.valueOf(permission.getOperation().getOperationId());
                String objId = String.valueOf(permission.getRBACObject().getObjectId());

                String var = tabSpace + tabSpace +
                        "permission" + idStr + " : permission(" + idStr + ", " + opId + ", " + objId + ");" +
                        " -- Permesso di " + permission.getOperation().getName() + " su " +
                        permission.getRBACObject().getObjectName() + "\n";
                mainModule = mainModule.concat(var);
            }

            for (Role role : roles) {
                String roleIdStr = String.valueOf(role.getRoleId());
                for (Permission permission : role.getPermissionList()) {
                    String permIdStr = String.valueOf(permission.getId());

                    String var = tabSpace + tabSpace + "rp" + roleIdStr + "_" + permIdStr + " : rolePermission(" +
                            roleIdStr + ", " + permIdStr + ");\n";
                    mainModule = mainModule.concat(var);
                }
            }

            for (RBACObject object : objects) {
                String idStr = String.valueOf(object.getObjectId());
                String var = tabSpace + tabSpace + "object" + idStr + " : " + "object(" + idStr + "); " +
                        "-- " + object.getObjectName() + "\n";
                mainModule = mainModule.concat(var);
            }

            for (Operation operation : operations) {
                String idStr = String.valueOf(operation.getOperationId());
                String var = tabSpace + tabSpace + "operation" + idStr + " : " + "operation(" + idStr + "); " +
                        "-- Operazione " + operation.getName() + "\n";
                mainModule = mainModule.concat(var);
            }


            nusmvCode = nusmvCode.concat(mainModule);
            nusmvCode = nusmvCode.concat(generateLTLFormula());
            return nusmvCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The method initialize all lists.
     */
    private static void initializeLists() {
        users = session.getUsers();
        roles = session.getRoles();

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

    /**
     * The method writes a new NuSMV module using the name, the variables and the domain provided
     * by the developer. Also it defines the assignement and the next rule.
     * @param name the name of the module
     * @param variables the variable inside the module
     * @param domains the list of domains, one for each variable
     * @param assignment the list of assignments, one for each variable
     * @param next the list of assignements for the next states.
     * @return a string representing the module
     */
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

    /**
     * The method writes the user module
     * @return the user module
     */
    private static String generateUserModule(){
        return generateModule("user",
                new String[]{"id", "activePerm"},
                new String[]{"0.." + roles.size(), "0.." + operations.size()},
                new String[]{"id", "activePerm"},
                new String[]{"id", "activePerm"});
    }

    /**
     * The method writes the role module
     * @return the role module
     */
    private static String generateRoleModule(){
        return generateModule(
                "role",
                new String[]{"id"},
                new String[]{"0.." + roles.size()},
                new String[]{"id"},
                new String[]{"id"});
    }

    /**
     * The method writes the module wich connect users to roles
     * @return the userRole module
     */
    private static String generateUserRoleModule(){
        return generateModule("userRole",
                new String[]{"user", "role", "active", "status"}, // variables
                new String[]{"0.." + users.size(), "0.." + roles.size(), "boolean", "{Aut, notAut}"}, // domains
                new String[]{"user", "role", "active", "status"}, //init
                new String[]{"user", "role", "active", "{Aut, notAut}"}); //next
    }

    /**
     * The method writes the permission module
     * @return the permission module
     */
    private static String generatePermissionModule(){
        return generateModule(
                "permission",
                new String[]{"permid", "operid", "objectid"},
                new String[]{"0.." + permissions.size(), "0.." + operations.size(), "0.." + objects.size()},
                new String[]{"permid", "operid", "objectid"},
                new String[]{"permid", "operid", "objectid"});
    }

    private static String generateRolePermissionModule(){
        return generateModule(
                "rolePermission",
                new String[]{"roleId", "permId"},
                new String[]{"0.." + roles.size(), "0.." + permissions.size()},
                new String[]{"roleId", "permId"},
                new String[]{"roleId", "permId"}
        );
    }

    /**
     * The method writes the object module
     * @return the object module
     */
    private static String generateObjectModule(){
        return generateModule(
                "object",
                new String[]{"id"},
                new String[]{"0.." + objects.size()},
                new String[]{"id"},
                new String[]{"id"});
    }

    /**
     * The method writes the operation module.
     * @return the operation module
     */
    private static String generateOperationModule(){
        return generateModule(
                "operation",
                new String[]{"id"},
                new String[]{"0..100"},
                new String[]{"id"},
                new String[]{"id"});
    }

    /**
     * Writes the rule of assignement to verify that all users that can execute
     * have an active role.
     * @return the LTL formula
     */
    private static String rulesOfAssignement(){
        StringBuilder result= new StringBuilder();
        for (User u : session.getUsers()) {
            result.append(tabSpace + "LTLSPEC\n" + tabSpace + "G (user").append(u.getUserId()).append(".activePerm != 0 -> (");
            for (Role r : u.getAuthRoles()){
                result.append("ur").append(u.getUserId()).append("_").append(r.getRoleId()).append(".active = TRUE | ");
            }
            result.append("FALSE));\n");
        }
        return result.toString();
    }

    /**
     * The method writes the rules of authorization. They verify that all active roles of users
     * are also authorized.
     * @return the LTL formula
     */
    private static String rulesOfAuthorization(){
        StringBuilder result= new StringBuilder();
        for (User u : session.getUsers()) {
            for (Role r : u.getAuthRoles()){
                result.append(tabSpace + "LTLSPEC\n" + tabSpace + "G !( ur").append(u.getUserId()).append("_").append(r.getRoleId()).append(".active = TRUE & ").append("ur").append(u.getUserId()).append("_").append(r.getRoleId()).append(".status = notAut );\n");
            }
        }
        return result.toString();
    }

    /*Rule of transaction authorization:
     *
     *verify that operations executed by users are always authorized for the active role
     * */
    private static String roleOfTransactionAuthorization(){
        StringBuilder result= new StringBuilder();
        for (User u : session.getUsers()) {
            result.append("LTLSPEC\n" + "G (user").append(u.getUserId()).append(".activePerm = 0 | user").append(u.getUserId()).append(".activePerm in (");
            for (Role r : roles){
                result.append(" (ur").append(u.getUserId()).append("_").append(r.getRoleId()).append(".status = NoAut ? 0 : ( ");
                for (Permission p : permissions) {
                    result.append("(rp").append(r.getRoleId()).append("_").append(p.getId()).append(".boolean ? permission").append(p.getId()).append(".permid : 0 )  union ");
                }
                result.append("0 ) ) union ");
            }
            result.append(" 0 ) );\n");
        }
        return result.toString();
    }

    // Versione per il caso di assegnamenti Ruoli-permessi immmodificabili

    private static String roleOfTransactionAuthorizationImmutable(){
        StringBuilder result= new StringBuilder();
        for (User u : session.getUsers()) {
            result.append(
                    tabSpace + "LTLSPEC\n" + tabSpace + "G (user").
                    append(u.getUserId()).
                    append(".activePerm = 0 | user").
                    append(u.getUserId()).
                    append(".activePerm in (");
            for (Role r : u.getAuthRoles()){
                result.append(" (ur").append(u.getUserId()).append("_").append(r.getRoleId()).append(".status = notAut ? 0 : ( ");
                for (Permission p : r.getPermissionList()) {
                    result.append("permission").append(p.getId()).append(".permid union ");
                }
                result.append("0 ) ) union ");
            }
            result.append(" 0 ) );\n");
        }
        return result.toString();
    }


    private static String generateLTLFormula(){
        String result = "";
        result += rulesOfAssignement();
        result += rulesOfAuthorization();
        result += roleOfTransactionAuthorizationImmutable();

        return result;
    }
}
