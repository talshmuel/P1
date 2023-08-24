package xml.reader.validator;

public class EntityException extends Exception{
    private final String msg;
    public EntityException(String message){this.msg=message;}

    @Override
    public String getMessage() {
        return msg;
    }
}
