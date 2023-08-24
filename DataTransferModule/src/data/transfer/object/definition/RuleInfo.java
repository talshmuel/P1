package data.transfer.object.definition;

import java.util.ArrayList;

public final class RuleInfo {
    String name;
    int ticks;
    double probability;
    int numOfActions;
    ArrayList<String> actions;

    public RuleInfo(String name, int ticks, double probability, int numOfActions, ArrayList<String> actions){
        this.name = name;
        this.numOfActions = numOfActions;
        this.probability = probability;
        this.ticks = ticks;
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "RuleInfo{" +'\n'+"      "+
                "name='" + name + '\'' +
                ", ticks=" + ticks +
                ", probability=" + probability +
                ", numOfActions=" + numOfActions +
                ", actions=" + actions +
                '}'+'\n';
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

    public ArrayList<String> getActions() {
        return actions;
    }
}
