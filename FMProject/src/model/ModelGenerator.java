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
    private static ArrayList<Role> RoleList = new ArrayList<Role>();

    // lista degli oggetti nel sistema
    private static ArrayList<RBACObject> RBACObjectList = new ArrayList<RBACObject>();

    // lista delle operazioni nel sistema
    private static ArrayList<Operation> OperationList = new ArrayList<Operation>();


    public static void parseStream(Node node, int level, String type, String output) throws Exception {

        //-----------------------------------------------------------------------------------------------------------------------------------------
        // Spaziatura ad albero per i livelli
        String space = "";
        for (int j = 0; j <= level; j++)
            if(level == 0)
                space += "\n         ";
            else
                space += "          ";
        //-----------------------------------------------------------------------------------------------------------------------------------------

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

                        // Stampo il nome dell'i-esimo elemento
                        System.out.print("\n\n" + space + "-->Nome dell'elemento: " + element.getNodeName());

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

                            System.out.print("      ****oggetto creato: " + " id = " + o.getObjectId() + "    nome = " + o.getObjectName() + "***");
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

                            System.out.print("      ****operazione creata: " + " id = " + o.getOperationId() + "    nome = " + o.getName() + "    descrizione = " + o.getDescription() + "***");
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

                            // istanzio il nuovo oggetto permesso
                            Permission p = new Permission( PermissionId, RBACo, operationP);
                            System.out.print("      ****permesso creato: " + " id = " + p.getId() + "    id_oggetto = " + p.getRBACObject().getObjectId() + "    id_operazione = " + p.getOperation().getOperationId() + "***");
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

                            Role r = new Role(RoleId, RoleName, new ArrayList<Permission>());

                            // mi salvo il nodo permissions dell'xml, che ha come sotto-elementi la lista dei permessi associati al ruolo
                            Node permissions = element.getChildNodes().item(3);

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

                                    // creo e aggiungo il nuovo permesso alla lista dei permessi del ruolo considerato
                                    Permission p = new Permission(PermissionId, RBACo, operationP);
                                    PermissionList.add(p);

                                    // aggiorno il ruolo r con la lista dei permessi associati
                                    r = new Role(RoleId, RoleName, PermissionList);

                                    // aggiungo r alla lista dei ruoli
                                    RoleList.add(r);

                                    System.out.print("      ****permesso aggiunto al ruolo: " + " id_permesso = " + p.getId() + "    id_oggetto = " + p.getRBACObject().getObjectId() + "    id_operazione = " + p.getOperation().getOperationId() + "***");

                                }
                            }
                            System.out.print("      ****ruolo creato: " + " id = " + r.getRoleId() + "    nome = " + r.getRoleName() + "***");
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
                                            System.out.print("      ****ruolo autorizzato aggiunto all'utente creato: " + " id_ruolo = " + r.getRoleId() + "    nome_ruolo = " + r.getRoleName() + "***");
                                        }
                                    }
                                }
                            }

                            //DA FINIRE POI CON LOROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                            // ruolo attivo e operazione a cavolo!!!
                           User u = new User(UserId, UserName, UserRoleList, UserRoleList.get(0),new Operation(1,"",""));

                            System.out.print("      ****utente creato: " + " id = " + UserId + "    nome = " + UserName + "***");

                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // Se ci sono attributi legati a questo elemento li stampo
                        if (element.hasAttributes()) {
                            for (int k = 0; k < element.getAttributes().getLength(); k++) {

                                Node attribute = element.getAttributes().item(k);

                                if (!attribute.getTextContent().equals(""))
                                    System.out.print("\n" + space + "   Nome dell'attributo: " + attribute.getNodeName() + "    Testo = " + attribute.getTextContent());
                                else
                                    System.out.print("\n" + space + "   Nome dell'attributo: " + attribute.getNodeName() + "    Testo = vuoto");

                            }
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------

                        // CASO BASE:
                        // Se il nodo ha un solo figlio, ed è anche un figlio di tipo text, allora l'algoritmo non scende nella ricorione
                        // e stampo il contenuto del figlio testuale
                        if ((element.getChildNodes().getLength() == 1) && (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
                            System.out.println("    Testo = " + element.getChildNodes().item(0).getTextContent());

                            // Si scende nell'albero se si ha almeno un figlio non testuale
                        else if (element.getChildNodes().getLength() > 0)
                            parseStream(element, level + 1, type, output);
                    }
                }
            }
        }
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

        System.out.println("\n\nNome dell'elemento radice: " + root.getNodeName());

        String type = "session";

        //Si parte con la ricorsione dalla radice
        parseStream(root, 0, type, "");
    }

    /*
    public Session createSession(String xmlFile){

    }*/
}


