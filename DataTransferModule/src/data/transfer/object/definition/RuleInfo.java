package data.transfer.object.definition;

import java.util.ArrayList;

public final class RuleInfo {
    String name;
    int ticks;
    double probability;
    int numOfActions;
    ArrayList<ActionInfo> actions;

    public RuleInfo(String name, int ticks, double probability, int numOfActions, ArrayList<ActionInfo> actions){
        this.name = name;
        this.numOfActions = numOfActions;
        this.probability = probability;
        this.ticks = ticks;
        this.actions = actions;
    }

    @Override
    public String toString() {
        return  "Rule name: "+name+"\n"+
                "Ticks: "+ticks+"\n"+
                "Probability: "+ probability+"\n";
    }

    public String getName() {
        return name;
    }

    public double getProbability() {
        return probability;
    }

    public int getNumOfActions() {
        return numOfActions;
    }

    public int getTicks() {
        return ticks;
    }

    public ArrayList<ActionInfo> getActions() {
        return actions;
    }
}
