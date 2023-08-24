package data.transfer.object.run.result;

import java.util.ArrayList;


public final class RunResultInfo {

    String runDate;
    int id;
    ArrayList<EntityResultInfo> entitiesResults;

    public RunResultInfo(ArrayList<EntityResultInfo> entitiesResults, int id, String runDate){
        this.entitiesResults = entitiesResults;
        this.id = id;
        this.runDate = runDate;
    }

    public int getId() {
        return id;
    }

    public ArrayList<EntityResultInfo> getEntitiesResults() {
        return entitiesResults;
    }

    public String getRunDate() {
        return runDate;
    }
}
