package UI.page.results;

public class SimulationResult {
    public enum SimulationState{PENDING, RUNNING, PAUSED, DONE, CANCELLED}

    SimulationState currentState;
    int id;

    public SimulationResult(int id){
        currentState = SimulationState.PENDING;
        this.id = id;
    }

    public void setCurrentState(SimulationState currentState) {
        this.currentState = currentState;
    }

    public SimulationState getCurrentState() {
        return currentState;
    }

    @Override
    public String toString() {
        return "Simulation run ID: "+id+" "+currentState;
    }

    public int getId() {
        return id;
    }
}
