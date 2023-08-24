package data.transfer.object;

public final class EndSimulationData {
    int runId;
    String endCondition;
    int endConditionVal;

    public EndSimulationData(int runId, String endCondition, int endConditionVal){
        this.endCondition = endCondition;
        this.runId = runId;
        this.endConditionVal = endConditionVal;
    }

    public int getRunId() {
        return runId;
    }

    public String getEndCondition() {
        return endCondition;
    }

    public int getEndConditionVal() {
        return endConditionVal;
    }
}
