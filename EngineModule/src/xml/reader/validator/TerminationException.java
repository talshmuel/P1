package xml.reader.validator;

public class TerminationException extends Exception{
    private final String msg;
    public TerminationException(String message){this.msg=message;}

    @Override
    public String getMessage() {
        return msg;
    }
}
