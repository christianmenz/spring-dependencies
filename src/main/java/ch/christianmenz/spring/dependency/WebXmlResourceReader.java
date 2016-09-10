package ch.christianmenz.spring.dependency;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class WebXmlResourceReader {

    public String[] readConfigContextLocations() throws XMLStreamException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        List<String> configFiles = new ArrayList<String>();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(new FileInputStream("src/main/webapp/WEB-INF/web.xml"));        
        XPath xpath = XPathFactory.newInstance().newXPath();        
        String config = xpath.evaluate("//init-param/param-name[text() = 'contextConfigLocation']/following-sibling::param-value/text()", document);        
        String[] lines = config.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!StringUtils.isEmpty(trimmedLine)) {
                configFiles.add(trimmedLine);    
            }           
        }                         
        return configFiles.toArray(new String[0]);
    }        

}
