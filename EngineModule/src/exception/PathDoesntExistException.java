package exception;

public class PathDoesntExistException extends Exception{
    @Override
    public String getMessage() {
        return "An error occurred: path doesn't exist!\n";
    }
}
