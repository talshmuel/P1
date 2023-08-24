package xml.reader.validator;

public class InvalidXMLFileNameException extends Exception {

    private final String EXCEPTION_MESSAGE = "File is not of XML type.\n";

    public InvalidXMLFileNameException() {}

    @Override
    public String getMessage(){
        return EXCEPTION_MESSAGE;
    }
}