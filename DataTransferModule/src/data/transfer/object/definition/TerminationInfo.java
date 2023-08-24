package data.transfer.object.definition;
public final class TerminationInfo {
    //public enum TerminationName {TICKS, SECONDS};
    String terminationCondition;
    int val;
    public TerminationInfo(String terminationCondition, int val){
        this.terminationCondition = terminationCondition;
        this.val = val;
    }

    @Override
    public String toString() {
        return "TerminationInfo{" +'\n'+"       "+
                "terminationCondition='" + terminationCondition + '\'' +
                ", val=" + val +
                '}'+'\n';
    }

    public int getVal() {
        return val;
    }

    public String getTerminationCondition() {
        return terminationCondition;
    }
}
