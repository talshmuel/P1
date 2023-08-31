package world.creator;

public class XMLFileException extends Exception{
    private final String msg;
    public XMLFileException(String message){
        this.msg=message;
    }
    @Override
    public String getMessage() {
        return msg;
    }

}