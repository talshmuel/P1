package run.manager;

import data.transfer.object.DataFromUser;
import data.transfer.object.run.result.RunResultInfo;
import exception.SimulationRunningException;
import world.World;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RunManager implements RunManagerInterface {


    World worldDefinition;
    Map<Integer, SingleRun> runs;//הרצה והID שלה

    public RunManager(){
        runs = new HashMap<>();
    }

    @Override
    public String getCurrentStateOfSpecificRun(int runId) {
        return String.valueOf(runs.get(runId).getCurrentState());
    }

    @Override
    public void cleanup() {
        if (worldDefinition != null)
            worldDefinition.cleanup();
        runs.clear();
    }

    @Override
    public RunResultInfo getSpecificRunResult(int runID) {
        return runs.get(runID).getRunResultInfo();
    }

    @Override
    public void setWorldDefinition(World worldDefinition) {
        this.worldDefinition = worldDefinition;
    }

    @Override
    public void runSimulation(DataFromUser dataFromUser) {

        try {
            SingleRun newRun = new SingleRun(worldDefinition, dataFromUser.getRunID());
            runs.put(dataFromUser.getRunID(),newRun);
            newRun.runSimulation(dataFromUser);
        } catch (SimulationRunningException | IOException | ClassNotFoundException | InterruptedException e){
            runs.get(dataFromUser.getRunID()).cancelRun(e.getMessage());
        }
    }

    @Override
    public void setUserControlOnSpecificRun(int runID, String userControl) {
        runs.get(runID).setUserControl(userControl);
    }
}
