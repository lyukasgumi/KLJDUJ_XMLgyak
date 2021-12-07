/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hu.domparse.KLJDUJ.QUERY;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

/**
 *
 * @author Domán
 */
public class DOMQueryKLJDUJ {

    /**
     * @param args the command line arguments
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, DOMException, ParseException {
// XML fájl beolvasása
        File xml = new File("XMLKLJDUJ.xml");

// XML fájl DOM document való formába való alakítása
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

// DOM document átalakítása DOM DocumentTraversal formába
        DocumentTraversal traversal = (DocumentTraversal) document;

        String searchTag = "Szemelyzet";
        String searchAttribute = "Menetido";
        String searchValue = "7";
        
        //függvény 3 attibumot vár a doksin es a traversen kivul
        //melyik tagben
        //milyen tulajdonsagat
        //milyen erteken keressuk
        listTagAttributesByValue(document, searchTag, searchAttribute, searchValue, traversal);
        System.out.println("");
        listTagAttributesByValue(document, "Jarmu", "Tankolni_kell", "1", traversal);

    }
    

    private static void listTagAttributesByValue(Document document, String searchTag, String searchAttribute, String searchValue, DocumentTraversal traversal) throws DOMException {
        
        //a dokumentumbol kigyujtjuk a keresett tageket
        NodeList elemList = document.getElementsByTagName(searchTag);
        
        //végig iterálunk rajtuk
        for (int i = 0; i < elemList.getLength(); i++) {
            
            //jelenlegu elem gyerek tagjeit (tulajdonságait) kigyujtjuk
            NodeList tagNodeList = elemList.item(i).getChildNodes();
            
            //végig iterálunk a tulajdonságokon
            for (int j = 0; j < tagNodeList.getLength(); j++) {
                
                //ha a jelenlegi tulajdonság neve megfelel a keresettnek ÉS az értéke is megfelel
               if (tagNodeList.item(j).getNodeName().equals(searchAttribute)
                    && tagNodeList.item(j).getTextContent().equals(searchValue)) {
                   
                   //Domtraverser segitsegevel kiiratjuk a keresett elem tulajdonsagait
                DomTraverser.traverseLevel(traversal.createTreeWalker(elemList.item(i),
                        NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true), "");
                
            } 
            }
            
        }
    }

    private static class DomTraverser {

        public static void traverseLevel(TreeWalker walker, String indent) {
            // kimentjük az aktuális csomópontot
            Node node = walker.getCurrentNode();

            // kiíratjuk a megfelelő metódussal
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // rekurzívan meghívjuk a bejárást a DOM fa eggyel mélyebben lévő csomópontjára,
            // majd azok testvér csomópontjaira
            for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
                traverseLevel(walker, indent + "    ");
            }

            walker.setCurrentNode(node);
        }

        private static void printElementNode(Node node, String indent) {
            System.out.print(indent + node.getNodeName());

            printElementAttributes(node.getAttributes());
        }

        private static void printElementAttributes(NamedNodeMap attributes) {
            int length = attributes.getLength();

            if (length > 0) {
                System.out.print(" (");

                for (int i = 0; i < length; i++) {
                    Node attribute = attributes.item(i);

                    System.out.printf("%s=%s%s", attribute.getNodeName(), attribute.getNodeValue(),
                            i != length - 1 ? ", " : "");
                }

                System.out.println(")");
            } else {
                System.out.println();
            }
        }

        private static void printTextNode(Node node, String indent) {
            String content_trimmed = node.getTextContent().trim();

            if (content_trimmed.length() > 0) {
                System.out.print(indent);
                System.out.printf("%s%n", content_trimmed);
            }
        }
    }
}
