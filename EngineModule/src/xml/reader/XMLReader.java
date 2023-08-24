package xml.reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import xml.reader.schema.generated.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import xml.reader.validator.*;

public class XMLReader {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "xml.reader.schema.generated";

    public PRDWorld validateXMLFileAndCreatePRDWorld(String fileName) throws InvalidXMLFileNameException, FileDoesntExistException {
        if (!fileName.toLowerCase().endsWith(".xml")){
            throw new InvalidXMLFileNameException();
        }
        PRDWorld prdWorld = fromXmlFileToObject(fileName);
        if(prdWorld != null){
            return prdWorld;
        } else {
            throw new FileDoesntExistException();
        }
    }

    public PRDWorld fromXmlFileToObject(String fullPath) {
        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            FileInputStream xmlFileInputStream = new FileInputStream(fullPath);

            // unmarshal XML into a root element object
            PRDWorld worldPRD = (PRDWorld) unmarshaller.unmarshal(xmlFileInputStream);
            return worldPRD;

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
