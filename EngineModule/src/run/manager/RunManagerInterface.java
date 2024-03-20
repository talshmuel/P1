package run.manager;

import data.transfer.object.DataFromUser;
import data.transfer.object.run.result.RunResultInfo;
import world.World;

public interface RunManagerInterface {
    void runSimulation(DataFromUser dataFromUser);
    void setWorldDefinition(World worldDefinition);
    void setUserControlOnSpecificRun(int runID, String userControl);
    RunResultInfo getSpecificRunResult(int runID);
    String getCurrentStateOfSpecificRun(int runId);


    void cleanup();
}
