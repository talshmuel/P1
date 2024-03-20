package data.transfer.object.definition;
public final class TerminationInfo {
    String terminationCondition;
    Integer val;

    public TerminationInfo(String terminationCondition, Integer val){
        this.terminationCondition = terminationCondition;
        this.val = val;
    }

    @Override
    public String toString() {

        String res = "By "+terminationCondition;
        if(terminationCondition!="user")
            res+= ": "+val+" "+terminationCondition;

        return res;

    }

    public int getVal() {
        return val;
    }

    public String getTerminationCondition() {
        return terminationCondition;
    }
}
