package xml.reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
//import xml.reader.schema.generated.v1.ObjectFactory;
//import xml.reader.schema.generated.v1.PRDWorld;
import xml.reader.validator.*;
import xml.reader.schema.generated.v2.*;

public class XMLReader {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "xml.reader.schema.generated";
    private final static String JAXB_XML_GAME_PACKAGE_NAME_NEW = "xml.reader.schema.generatedNew";

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
            return (PRDWorld) unmarshaller.unmarshal(xmlFileInputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    // old version
    /*public PRDWorld fromXmlFileToObject(String fullPath) { // old version
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
    }*/
}
