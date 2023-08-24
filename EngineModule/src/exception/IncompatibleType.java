package exception;

public class IncompatibleType extends Exception {
    @Override
    public String getMessage() {
        return "Error: Attempt was made to enter a value that does not match the variable type";
    }
}
