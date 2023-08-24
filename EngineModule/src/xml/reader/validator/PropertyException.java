package xml.reader.validator;

public class PropertyException extends Exception{
    private final String msg;
    public PropertyException(String message){this.msg=message;}

    @Override
    public String getMessage() {
        return msg;
    }
}
