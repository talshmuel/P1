package xml.reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import world.creator.XMLFileException;
import xml.reader.schema.generated.v2.*;

public class XMLReader {
    public PRDWorld validateXMLFileAndCreatePRDWorld(String fileName) throws XMLFileException {
        if (!fileName.toLowerCase().endsWith(".xml")){
            throw new XMLFileException("Loading file error: File is not an XML file!");
        }
        PRDWorld prdWorld = fromXmlFileToObject(fileName);
        if(prdWorld != null){
            return prdWorld;
        } else {
            throw new XMLFileException("Loading file error: File doesn't exist!");
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
}
