package XmlFeladatKLJDUJ;

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
        // XML fĂˇjl betĂˇrolĂˇsa
        File xml = new File("XMLKLJDUJ.xml");

        // DOM doc kĂ©szĂ­tĂ©se az xml bĹ‘l
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xml);

        // a DOM doc adatainak mĂłdosĂ­tĂˇsa
        DomModifier.modifyDom(document);

        // DOM Traversal kĂ©szĂ­tĂ©se a DOM DocumentbĹ‘l
        DocumentTraversal traversal = (DocumentTraversal) document;

        // DOM TreeWalker inicializĂˇlĂˇsa        
        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        // rekurzĂ­v DOM bejĂˇrĂˇs 
        DomTraverser.traverseLevel(walker, "");

        //modositott XML lĂ©trehozĂˇsa XMLOydqp1.updated.xml fĂˇjlkĂ©nt
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("XMLKLJDUJ.updated.xml"));
        transformer.transform(source, result);
    }

    private static class DomModifier {
        public static void modifyDom(Document document) throws XPathExpressionException, DOMException, ParseException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            // 1.)KĂˇdĂˇr BĂˇlint az utolsĂł napra hagyta a XML beadandĂł elkĂ©szĂ­tĂ©sĂ©t, ezĂ©rt virgĂˇcsot kell kapnia.
            // XPath segĂ­tsĂ©gĂ©vel lekĂ©rdezzĂĽk a megfelelĹ‘ elemet/csomĂłpontot a DOM
            // documentbĂłl
            Node gyermek = (Node) xpath.evaluate("//gyermek[./nev='KĂˇdĂˇr BĂˇlint']/virgacs_kell",
                    document, XPathConstants.NODE);

            gyermek.setTextContent("1");

            // 2.) Mivel bevezettek egy Ăşj biztonsĂˇgi elĹ‘irĂˇst, ezĂ©rt a legalĂˇbb 
            //500.000-ret futott szĂˇnok teherbĂ­rĂˇsĂˇt 20% -kal csĂ¶kkenteni kell.
            NodeList jarmuvek = (NodeList) xpath.evaluate("//jarmu[./futasteljesitmeny>=500000]/terhelhetoseg", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < jarmuvek.getLength(); i++) {
                Node jarmu = jarmuvek.item(i);


                String teherbiras = (jarmu.getTextContent());
                int teherbiras1 = Integer.parseInt(teherbiras);
                jarmu.setTextContent(Double.toString(teherbiras1 * 0.8));
            }
        }
    }

    private static class DomTraverser {
        public static void traverseLevel(TreeWalker walker, String indent) {
            // aktuĂˇlis csomĂłpont
            Node node = walker.getCurrentNode();

            // kiĂ­ratĂˇs
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                printElementNode(node, indent);
            } else {
                printTextNode(node, indent);
            }

            // rekurzĂ­van meghĂ­vjuk a bejĂˇrĂˇst a DOM fa eggyel mĂ©lyebben lĂ©vĹ‘ csomĂłpontjĂˇra,
            // majd azok testvĂ©r csomĂłpontjaira
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