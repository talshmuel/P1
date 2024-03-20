package data.transfer.object;

import java.util.HashMap;
import java.util.Map;

public class DataFromUser {

    int runID;
    Map<String, Integer> population;
    Map<String, Object> environment;

    public DataFromUser(int runID){

       this.runID = runID;
        population = new HashMap<>();
        environment = new HashMap<>();
    }



    public void setEnvironment(String varName, Object varValue) {
        environment.put(varName, varValue);
    }

    public void setPopulation(String entityName, Integer amount) {
        population.put(entityName, amount);
    }

    public Map<String, Integer> getPopulation() {
        return population;
    }

    public Map<String, Object> getEnvironment() {
        return environment;
    }

    public int getRunID() {
        return runID;
    }
}
