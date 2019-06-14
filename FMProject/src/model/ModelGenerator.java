package src.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import src.model.elements.Session;
import src.model.elements.User;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class ModelGenerator {


    public static void parseStream() throws Exception {
        // Costruiamo una factory per processare il nostro flusso di dati
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Instanziamo un nuovo Documento
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Carichiamo il nostro documento da un file (assicuratevi sia nel path giusto)
        Document doc = builder.parse(new File("C:\\Users\\theje\\IdeaProjects\\FMProject\\base.xml"));

        // Prendiamo il primo nodo - come suggerisce il metodo - la radice
        Node root = doc.getFirstChild();

        // Iteriamo per ogni nodo presente nella lista dei nodi della radice
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            // Sapendo che il primo nodo è il nodo libro procediamo iterando nei suoi nodi figli
            Node libro = root.getChildNodes().item(i);

            //Se il nodo è un Elemento allora ne stampiamo il nome, il testo contenuto e gli attributi se presenti
            for (int j = 0; j < libro.getChildNodes().getLength(); j++) {
                Node element = libro.getChildNodes().item(j);
                if (element.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println("\n\nNome dell'elemento: " + element.getNodeName() + "\n     Testo = " + element.getTextContent());
                    if (element.hasAttributes()) {
                        for (int k = 0; k < element.getAttributes().getLength(); k++) {
                            Node attribute = element.getAttributes().item(k);
                            System.out.print("\t              Nome dell'attributo: " + attribute.getNodeName() + " - Valore del testo = " + attribute.getTextContent());
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        parseStream();
    }

    /*
    public Session createSession(String xmlFile){

    }*/
}


