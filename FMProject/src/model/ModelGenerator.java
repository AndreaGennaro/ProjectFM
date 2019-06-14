package src.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


public class ModelGenerator {


    public static void parseStream(Node node, int level) throws Exception {

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
        if(node.getChildNodes().getLength() > 0)
            for(int i = 0; i <= node.getChildNodes().getLength(); i++) {

                Node element = node.getChildNodes().item(i);


                if(element != null) {
                    if (element.getNodeType() == Node.ELEMENT_NODE){



                        System.out.println("\n"+space + "-->Nome dell'elemento: " + element.getNodeName());

                        //-----------------------------------------------------------------------------------------------------------------------------------------
                        // Se ci sono attributi legati a questo elemento li stampo
                        if (element.hasAttributes()) {
                            for (int k = 0; k < element.getAttributes().getLength(); k++) {

                                Node attribute = element.getAttributes().item(k);

                                if (!attribute.getTextContent().equals(""))
                                    System.out.println(space + "   Nome dell'attributo: " + attribute.getNodeName() + "    Testo = " + attribute.getTextContent());
                                else
                                    System.out.println(space + "   Nome dell'attributo: " + attribute.getNodeName() + "    Testo = vuoto");

                            }
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------

                        // CASO BASE:
                        // Se il nodo ha un solo figlio, ed è anche un figlio di tipo text, allora l'algoritmo non scende nella ricorione
                        // e stampo il contenuto del figlio testuale
                        if((element.getChildNodes().getLength() == 1) && (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
                            System.out.println(space + "   contenuto testuale foglia = " + element.getChildNodes().item(0).getTextContent());

                        // Si scende nell'albero se si ha almeno un figlio non testuale
                        else if (element.getChildNodes().getLength() > 0)
                            parseStream(element, level + 1);
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
        Document doc = builder.parse(new File("C:\\Users\\theje\\IdeaProjects\\FMProject\\base.xml"));

        // Prendiamo il primo nodo - come suggerisce il metodo - la radice
        Node root = doc.getFirstChild();

        System.out.println("\n\nNome dell'elemento radice: " + root.getNodeName());

        //Si parte con la ricorsione dalla radice
        parseStream(root, 0);
    }

    /*
    public Session createSession(String xmlFile){

    }*/
}


