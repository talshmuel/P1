package data.transfer.object.run.result;

import java.util.ArrayList;


public final class RunResultInfo {

    int ticks;
    long startTime;
    int runID;
    long totalTime;
    String currentState;
    String cancelReason;

    WorldResultInfo currentWorldResult;//just for results of RUNNING runs

    ArrayList<WorldResultInfo> worldResultAtEveryTick;//just for results of DONE runs

    public RunResultInfo(int ticks, long startTime, long totalTime, int runID, String currentState,WorldResultInfo currentWorldResult, ArrayList<WorldResultInfo> worldResultAtEveryTick, String cancelReason) {
        this.ticks = ticks;
        this.startTime = startTime;
        this.totalTime = totalTime;
        this.runID = runID;
        this.currentState = currentState;
        this.currentWorldResult = currentWorldResult;
        this.worldResultAtEveryTick = worldResultAtEveryTick;
        this.cancelReason = cancelReason;
    }

    public int getRunID() {
        return runID;
    }

    public int getTicks() {
        return ticks;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getCurrentState() {
        return currentState;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public WorldResultInfo getCurrentWorldResult() {
        return currentWorldResult;
    }

    public ArrayList<WorldResultInfo> getWorldResultAtEveryTick() {
        return worldResultAtEveryTick;
    }

    public String getCancelReason() {
        return cancelReason;
    }
}
