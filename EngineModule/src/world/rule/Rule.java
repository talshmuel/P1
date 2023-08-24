package world.rule;

import world.rule.action.Action;

import java.io.Serializable;
import java.util.ArrayList;

public class Rule implements Serializable {
    String name;
    ArrayList<Action> actions;
    double probability;
    int ticks;

    public Rule(String name, ArrayList<Action> actions, double probability, int ticks){
        this.name=name;
        this.actions=actions;
        this.probability=probability;
        this.ticks=ticks;
    }

    public String getName() {
        return name;
    }

    public int getTicks() {
        return ticks;
    }

    public double getProbability() {
        return probability;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public int getNumOfActions(){
        return actions.size();
    }
}
