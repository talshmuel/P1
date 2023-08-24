package exception;


public class DivisionByZeroException extends Exception {
    @Override
    public String getMessage() {
        return "Error: Attempt to divide by zero";
    }
}