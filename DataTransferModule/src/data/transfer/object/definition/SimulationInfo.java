package data.transfer.object.definition;

import java.util.ArrayList;
import java.util.Map;

public final class SimulationInfo {
    ArrayList<EntityInfo> entities;
    ArrayList<RuleInfo> rules;
    ArrayList<TerminationInfo> endConditions;
    Map<String, PropertyInfo> environmentVariables;

    Integer numOfThreads;
    Integer gridSize;


    public SimulationInfo(ArrayList<EntityInfo> entities, ArrayList<RuleInfo> rules, ArrayList<TerminationInfo> endConditions,
                          Map<String, PropertyInfo> environmentVariables, Integer numOfThreads, Integer gridSize){
        this.endConditions = endConditions;
        this.entities = entities;
        this.rules = rules;
        this.environmentVariables = environmentVariables;
        this.numOfThreads = numOfThreads;
        this.gridSize = gridSize;
    }

    @Override
    public String toString() {
        return "SimulationInfo{" +'\n'+"        "+
                "entities=" + entities +
                ", rules=" + rules +
                ", endConditions=" + endConditions +
                ", environmentVariables=" + environmentVariables +
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

    public Map<String, PropertyInfo> getEnvironmentVariables() {
        return environmentVariables;
    }

    public Integer getNumOfThreads() {
        return numOfThreads;
    }

    public Integer getGridSize() {
        return gridSize;
    }
}
