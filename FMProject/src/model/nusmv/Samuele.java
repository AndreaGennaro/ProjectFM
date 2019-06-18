package src.model.nusmv;

import src.model.elements.*;

public class Samuele {

    Session session= new Session(null);

    /*Rule of assignment:
    *
    *verify that all users that can execute an operation have an active role
    * */
    private String roass(){
        String result="";
        for (User u : session.getUsers()) {
            result += "LTLSPEC\n" +
                      "G (u"+u.getUserId()+".activePerm != 0 -> (";
            for (Role r : session.getRoles()){
                result += "ur"+u.getUserId()+"_"+r.getRoleId()+".active = TRUE | ";
            }
            result += "FALSE));\n";
        }
        return result;
    }

    /*Rule of authorization:
     *
     *verify that all the active roles of users are also authorized
     * */
    private String roaut(){
        String result="";
        for (User u : session.getUsers()) {
            for (Role r : session.getRoles()){
                result += "LTLSPEC\n" +
                          "G !( ur"+u.getUserId()+"_"+r.getRoleId()+".active = TRUE & " +
                          "ur"+u.getUserId()+"_"+r.getRoleId()+".status = notAut );\n";
            }
        }
        return result;
    }

    /*Rule of transaction authorization:
     *
     *verify that operations executed by users are always authorized for the active role
     * */
    private String rota(){
        String result="";
        for (User u : session.getUsers()) {
            result += "LTLSPEC\n" +
                      "G (u" + u.getUserId() + ".activePerm = 0 | u" + u.getUserId() + ".activePerm in (";
            for (Role r : session.getRoles()){
                result += " (ur"+u.getUserId()+"_"+r.getRoleId()+".status = NoAut ? 0 : ( ";
                for (Permission p : session.getPermissions) {
                    result += "(rp"+r.getRoleId()+"_"+p.getId()+".boolean ? p"+p.getId()+".permid : 0 )  union ";
                }
                result += "0 ) ) union ";
            }
            result += " 0 ) );\n";
        }
        return result;
    }

    Versione per il caso di assegnamenti Ruoli-permessi immmodificabili

    private String rota(){
        String result="";
        for (User u : session.getUsers()) {
            result += "LTLSPEC\n" +
                      "G (u" + u.getUserId() + ".activePerm = 0 | u" + u.getUserId() + ".activePerm in (";
            for (Role r : session.getRoles()){
                result += " (ur"+u.getUserId()+"_"+r.getRoleId()+".status = NoAut ? 0 : ( ";
                for (Permission p : r.getPermissionList()) {
                    result += "p"+p.getId()+".permid union ";
                }
                result += "0 ) ) union ";
            }
            result += " 0 ) );\n";
        }
        return result;
    }


    public String generateLTLFormula(){
        String result="";
        result += roass();
        result += roaut();
        result += rota();

        return result;
    }
}
