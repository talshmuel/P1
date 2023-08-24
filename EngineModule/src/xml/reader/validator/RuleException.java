package xml.reader.validator;

public class RuleException extends Exception{
    private final String msg;
    public RuleException(String message){this.msg=message;}

    @Override
    public String getMessage() {
        return msg;
    }
}
