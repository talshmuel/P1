package exception;

public class SimulationRunningException extends Exception {
    private final String msg;
    public SimulationRunningException(String message){
        this.msg=message;
    }
    @Override
    public String getMessage() {
        return msg;
    }
}
