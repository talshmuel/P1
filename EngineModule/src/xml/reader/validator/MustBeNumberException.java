package xml.reader.validator;

public class MustBeNumberException extends Exception{
    private final String actionName;

    public MustBeNumberException(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public String getMessage(){
        return "An error occurred: " + actionName + " action must receive a number!";
    }
}
