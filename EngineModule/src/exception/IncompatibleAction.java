package exception;

public class IncompatibleAction extends Exception{
    @Override
    public String getMessage() {
        return "Error: Attempt was made to perform an Action that does not match the type of the variable";
    }
}
