package data.transfer.object.definition;

import java.util.ArrayList;

public final class SimulationInfo {
    ArrayList<EntityInfo> entities;
    ArrayList<RuleInfo> rules;
    ArrayList<TerminationInfo> endConditions;

    public SimulationInfo(ArrayList<EntityInfo> entities, ArrayList<RuleInfo> rules, ArrayList<TerminationInfo> endConditions){
        this.endConditions = endConditions;
        this.entities = entities;
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "SimulationInfo{" +'\n'+"        "+
                "entities=" + entities +
                ", rules=" + rules +
                ", endConditions=" + endConditions +
                '}'+'\n';
    }

    public ArrayList<EntityInfo> getEntities() {
        return entities;
    }

    public ArrayList<RuleInfo> getRules() {
        return rules;
    }

    public ArrayList<TerminationInfo> getEndConditions() {
        return endConditions;
    }
}
