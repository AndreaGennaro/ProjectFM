package src.model.nusmv;

import src.model.ModelGenerator;
import src.model.elements.Session;

public class ModelTranslator {

    private Session session;

    private static String tabspace = "    ";

    /**
     * The class generate the NuSMV code from the Session object.
     * @return the NuSMV code as a string.
     */
    public static String fromModelToNuSMV(String sessionFilepath){
        // session = ModelGenerator.createSession();

        String nusmvCode = "";

        String userModule = generateUserModule();
        String roleModule = generateRoleModule();
        String userRoleModule = generateUserRoleModule();

        return null;
    }

    private static String generateModule(String name, String[] variables, String[] domains, String[] assignment){
        String module = "MODULE " + name + "(";

        for(int i = 0; i < variables.length; i++){
            String var = variables[i];
            module = module.concat("new" + var);
            if(i < variables.length - 1){
                module = module.concat(", ");
            }
        }

        module = module.concat(")\n");
        module = module.concat(tabspace + "VAR\n");
        for(int i = 0; i < variables.length; i++){
            module = module.concat(tabspace + tabspace + variables[i] + " : " + domains[i] + ";\n");
        }

        module = module.concat(tabspace + "ASSIGN\n");
        for (int i = 0; i < variables.length; i++) {
            String variable = variables[i];
            module = module.concat(tabspace + tabspace + "init(" + variable + ") := new" + assignment[i] + ";\n");
        }

        return module;
    }

    private static String generateUserModule(){
        return generateModule("user", new String[]{"id"}, new String[]{"0..100"}, new String[]{"id"});
    }

    private static String generateRoleModule(){
        return generateModule("role", new String[]{"id"}, new String[]{"0..100"}, new String[]{"id"});
    }

    private static String generateUserRoleModule(){
        return generateModule("userRole", new String[]{"user", "role", "state", "active"},
                new String[]{"0..100", "0..100", "{notAut, Aut}", "boolean"},
                new String[]{"user", "role", "state", "active"});
    }

    private static String generatePermissionModule(){
        return null;
    }

}
