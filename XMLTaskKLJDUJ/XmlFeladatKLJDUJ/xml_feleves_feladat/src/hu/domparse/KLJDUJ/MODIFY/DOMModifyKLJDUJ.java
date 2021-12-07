/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hu.domparse.KLJDUJ.MODIFY;

/**
 *
 * @author Domán
 */

import java.io.*;
import java.text.ParseException;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;

public class DOMModifyKLJDUJ {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, DOMException, ParseException, TransformerException {
        // XML fájl betárolása
        File xml = new File("XMLkljduj.xml");

        // DOM doc készítése az xml ből
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM doc adatainak módosítása
        DomModifier.modifyDom(document);

        // DOM Traversal készítése a DOM Documentbóll
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializálása        
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // rekurzív DOM bejárás
        DomTraverser.traverseLevel(walker, "");

        //módosított XML létrehozása XMLkljduj.updated.xml fájlként
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("XMLkljduj.updated.xml"));
        transformer.transform(source, result);
    }

    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // Sirankó Boldizsár nem vallotta be, hogy találkozott egy koronás személlyel így kontaktnak számít
            // XPath segítségével lekérdezzük a megfelelő elemet/csomópontot a DOM
            // documentból
            Node Szemely = (Node) xpath.evaluate("//Szemely[./nev='Sirankó Boldizsár']/Kontakt",
                    document, XPathConstants.NODE);
            Szemely.setTextContent("1");
            
            // Mivel egyre több helyre kell menni naponta bevezették, 
            // hogy legalább 60%-os uzemanyag szintnek kell lenni a járművekben
            
            NodeList Szintek = (NodeList) xpath.evaluate("//Jarmu[./Uzemanyagszint<60]/Tankolni_kell", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < Szintek.getLength(); i++) {
                Node jarmu = Szintek.item(i);


                String tankolni_kell = (jarmu.getTextContent());
                
                int tankolni_kell2 = Integer.parseInt(tankolni_kell);
                if(tankolni_kell2 == 0){
                    tankolni_kell2+=1;
                }
                jarmu.setTextContent(Double.toString(tankolni_kell2));
                
            }
        }
    }

    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            // aktuális csomópont
            Node node = walker.getCurrentNode();

            // kiíratás
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }


            // rekurívan meghívjuk a bejárást a DOM fa egyel mélyebb csomópontjára,
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
                System.out.print(" [ ");

                for (int i = 0; i < length; i++) {
                    Node attribute = attributes.item(i);

                    System.out.printf("%s=%s%s", attribute.getNodeName(), attribute.getNodeValue(),
                            i != length - 1 ? ", " : "");
                }

                System.out.println(" ]");
            } else {
                System.out.println();
            }
        }

        private static void printTextNode(Node node, String indent) {
            String content_trimmed = node.getTextContent().trim();

            if (content_trimmed.length() > 0) {
                System.out.print(indent);
                System.out.printf("{ %s }%n", content_trimmed);
            }
        }
    }   
   
}