package xml.reader.validator;

public class EnvironmentException extends Exception{
    private final String msg;
    public EnvironmentException(String message){this.msg=message;}

    @Override
    public String getMessage() {
        return msg;
    }
}
