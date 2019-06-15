package src.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import src.model.elements.*;
import src.model.elements.RBACObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;


public class ModelGenerator {

    // lista dei ruoli nel sistema
    private static ArrayList<Role> RoleList = new ArrayList<>();

    // lista degli oggetti nel sistema
    private static ArrayList<RBACObject> RBACObjectList = new ArrayList<>();

    // lista delle operazioni nel sistema
    private static ArrayList<Operation> OperationList = new ArrayList<>();


    private static void parseStream(Node node, int level, String type){

        // PASSO RICORSIVO:
        // Se il nodo ha figli
        if(node.getChildNodes().getLength() > 0) {
            for (int i = 0; i <= node.getChildNodes().getLength(); i++) {

                // prendo il primo sottonodo
                Node element = node.getChildNodes().item(i);

                if (element != null) {
                    if (element.getNodeType() == Node.ELEMENT_NODE) {

                        // Se l'elemento definisce un'insieme di elementi, allora modifico il tipo nella ricorsione
                        if(element.getNodeName().equals("objects") || element.getNodeName().equals("operations")
                                || element.getNodeName().equals("permissions") && !type.equals("roles") || element.getNodeName().equals("roles") && !type.equals("users")
                                || element.getNodeName().equals("users")){
                            type = element.getNodeName();
                        }

                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // NEW OBJECT
                        // Se sto scorrendo la lista degli oggetti e devo aggiungere al sistema l'elemento attuale come oggetto
                        if(type.equals("objects") && element.getNodeName().equals("RBACObject")){

                            // mi ricavo l'id dell'oggetto
                            int ObjectId = Integer.parseInt(element.getAttributes().item(0).getTextContent());

                            // mi ricavo il nome dell'oggetto
                            String ObjectName = element.getChildNodes().item(1).getTextContent();

                            // istanzio il nuovo oggetto e lo aggiungo alla lista degli oggetti
                            RBACObject o = new RBACObject( ObjectId, ObjectName);
                            RBACObjectList.add(o);

                            System.out.println(" new OBJECT: " + "\n        id = " + o.getObjectId() + "\n        name = " + o.getObjectName() + "\n");
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // NEW OPERATION
                        // Se sto scorrendo la lista delle operazioni e devo aggiungere al sistema l'elemento attuale come operazione
                        else if(type.equals("operations") && element.getNodeName().equals("operation")){

                            // mi ricavo l'id dell'operazione
                            int OperationId = Integer.parseInt(element.getAttributes().item(0).getTextContent());

                            // mi ricavo il nome dell'operazione
                            String OperationName = element.getChildNodes().item(1).getTextContent();

                            // mi ricavo il tipo dell'operazione
                            String OperationDescription = element.getChildNodes().item(3).getTextContent();

                            // istanzio la nuova operazione e la aggiungo alla lista delle operazioni possibili nel sistema
                            Operation o = new Operation( OperationId, OperationName, OperationDescription);
                            OperationList.add(o);

                            System.out.println(" new OPERATION: " + "\n        id = " + o.getOperationId() + "\n        name = " + o.getName() + "\n        description = " + o.getDescription() + "\n");
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // NEW PERMISSION
                        // Se sto scorrendo la lista dei permessi e devo aggiungere al sistema l'elemento attuale come permesso
                        else if(type.equals("permissions") && element.getNodeName().equals("permission")){

                            // mi ricavo l'id del permesso
                            int PermissionId = Integer.parseInt(element.getAttributes().item(0).getTextContent());

                            // mi ricavo l'id dell'oggetto associato al permesso
                            int RBACObjectId = Integer.parseInt(element.getChildNodes().item(1).getTextContent());

                            // mi ricavo l'id dell'operazione associata al permesso
                            int OperationId = Integer.parseInt(element.getChildNodes().item(3).getTextContent());

                            // createPermission mi crea e ritorna l'oggetto permesso conoscendo l'id dell'oggetto, dell'operazione e del nuovo permesso
                            Permission p = createPermission(RBACObjectId,OperationId,PermissionId);

                            System.out.println(" new PERMISSION: " + "\n        id = " + p.getId() + "\n        object_id = " + p.getRBACObject().getObjectId() + "\n        operation_id = " + p.getOperation().getOperationId() + "\n");
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // NEW ROLE
                        // Se sto scorrendo la lista dei ruoli e devo aggiungere al sistema l'elemento attuale come ruolo
                        else if(type.equals("roles") && element.getNodeName().equals("role")) {

                            // mi ricavo l'id del ruolo
                            int RoleId = Integer.parseInt(element.getAttributes().item(0).getTextContent());

                            // mi ricavo il nome del ruolo
                            String RoleName = element.getChildNodes().item(1).getTextContent();

                            // istanzio la lista dei permessi
                            ArrayList<Permission> PermissionList = new ArrayList<>();

                            Role r = new Role(RoleId, RoleName, new ArrayList<>());

                            // mi salvo il nodo permissions dell'xml, che ha come sotto-elementi la lista dei permessi associati al ruolo
                            Node permissions = element.getChildNodes().item(3);

                            StringBuilder outPermission = new StringBuilder();

                            // mi ricavo la lista dei permessi associati al ruolo
                            for (int k = 0; k < permissions.getChildNodes().getLength(); k++) {

                                if (permissions.getChildNodes().item(k).getNodeName().equals("permission")) {

                                    // considero il k-esimo figlio dell'elemento permissions, ovvero il k-esimo permesso tra quelli associati al ruolo
                                    // mi ricavo l'id del permesso
                                    int PermissionId = Integer.parseInt(permissions.getChildNodes().item(k).getAttributes().item(0).getTextContent());

                                    // mi ricavo l'id dell'oggetto associato al permesso
                                    int RBACObjectId = Integer.parseInt(permissions.getChildNodes().item(k).getChildNodes().item(1).getTextContent());

                                    // mi ricavo l'id dell'operazione associata al permesso
                                    int OperationId = Integer.parseInt(permissions.getChildNodes().item(k).getChildNodes().item(3).getTextContent());

                                    // createPermission mi ricostruisce e ritorna l'oggetto permesso conoscendo l'id dell'oggetto, dell'operazione e del nuovo permesso
                                    Permission p = createPermission(RBACObjectId,OperationId,PermissionId);

                                    // aggiorno il ruolo r con la lista dei permessi associati
                                    r = new Role(RoleId, RoleName, PermissionList);

                                    // aggiungo r alla lista dei ruoli
                                    RoleList.add(r);

                                    outPermission.append("        permission added to the role: " + "\n                   permission_id = ").append(p.getId()).append("\n                   object_id = ").append(p.getRBACObject().getObjectId()).append("\n                   operation_id = ").append(p.getOperation().getOperationId()).append("\n");
                                }
                            }

                            System.out.println(" new ROLE: " + "\n        id = " + r.getRoleId() + "\n        name = " + r.getRoleName() + "\n" + outPermission);

                        }

                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // NEW USER
                        // Se sto scorrendo la lista degli utenti e devo aggiungere al sistema l'elemento attuale come nuovo utente
                        if(type.equals("users") && element.getNodeName().equals("user")) {

                            // mi ricavo l'id dell'utente
                            int UserId = Integer.parseInt(element.getAttributes().item(0).getTextContent());

                            // mi ricavo il nome dell'utente
                            String UserName = element.getChildNodes().item(1).getTextContent();

                            // istanzio la lista dei ruoli autorizzati per l'utente considerato
                            ArrayList<Role> UserRoleList = new ArrayList<>();

                            // mi salvo il nodo roles dell'xml, che ha come sotto-elementi la lista dei ruoli autorizzati per l'utente considerato
                            Node roles = element.getChildNodes().item(3);

                            StringBuilder outUser = new StringBuilder();

                            // mi ricavo la lista dei ruoli associati all'utente
                            for (int k = 0; k < roles.getChildNodes().getLength(); k++) {

                                if (roles.getChildNodes().item(k).getNodeName().equals("role")) {
                                    // considero il k-esimo figlio dell'elemento roles, ovvero il k-esimo ruolo tra quelli associati all'utente
                                    // mi ricavo l'id del ruolo
                                    int RoleId = Integer.parseInt(roles.getChildNodes().item(k).getChildNodes().item(0).getTextContent());

                                    // aggiungo alla lista dei ruoli dell'utente il ruolo il cui id è scritto nell'xml
                                    for (Role r: RoleList) {
                                        if(RoleId == r.getRoleId()) {
                                            UserRoleList.add(r);

                                            outUser.append("        authorized role added to the user: " + "\n                   role_id = ").append(r.getRoleId()).append("\n                   role_name = ").append(r.getRoleName()).append("\n");
                                        }
                                    }
                                }
                            }


                            //DA FINIRE POI CON LOROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                            // ruolo attivo e operazione a cavolo!!!

                            new User(UserId, UserName, UserRoleList, UserRoleList.get(0),new Operation(1,"",""));

                            System.out.println(" new USER: " + "\n        id = " + UserId + "\n        name = " + UserName + "\n" + outUser);
                        }

                        // Si scende nell'albero se si ha almeno un figlio non testuale
                        if (element.getChildNodes().getLength() > 0)
                            parseStream(element, level + 1, type);
                    }
                }
            }
        }
    }

    // metodo statico che costruisce e ritorna un permesso
    private static Permission createPermission(int RBACObjectId, int OperationId, int PermissionId){
        // inizializzo l'oggetto di cui mi ricaverò il nome dalla lista statica
        RBACObject RBACo = new RBACObject(RBACObjectId,"");

        for (RBACObject obj: RBACObjectList) {
            if(obj.getObjectId() == RBACObjectId)
                RBACo = obj;
        }

        // inizializzo il permesso di cui mi ricaverò il nome e la descrizione dalla lista statica
        Operation operationP = new Operation(OperationId,"", "");

        for (Operation op: OperationList) {
            if(op.getOperationId() == OperationId)
                operationP = op;
        }

        // ritorno il nuovo oggetto permesso
        return new Permission( PermissionId, RBACo, operationP);
    }

    public static void main(String[] args) throws Exception {
        // Costruiamo una factory per processare il nostro flusso di dati
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Instanziamo un nuovo Documento
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Carichiamo il nostro documento da un file (assicuratevi sia nel path giusto)
        Document doc = builder.parse(new File("C:\\Users\\theje\\IdeaProjects\\ProjectFM\\FMProject\\base.xml"));

        // Prendiamo il primo nodo - come suggerisce il metodo - la radice
        Node root = doc.getFirstChild();

        System.out.println("\n--------------------------------------------------------------------------------------------------------------\n new SESSION" +
                "\n--------------------------------------------------------------------------------------------------------------\n");

        //Si parte con la ricorsione dalla radice session
        parseStream(root, 0, "session");
    }

    /*
    public Session createSession(String xmlFilePath){

    }*/
}


