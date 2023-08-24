package xml.reader.validator;

public class FileDoesntExistException extends Exception {
    private final String EXCEPTION_MESSAGE = "An error occurred: File doesn't exist!.\n";

    public FileDoesntExistException() {}

    @Override
    public String getMessage(){
        return EXCEPTION_MESSAGE;
    }
}
