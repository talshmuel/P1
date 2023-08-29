package data.transfer.object;

import java.util.HashMap;
import java.util.Map;

public class DataFromUser {
    Map<String, Integer> population;
    Map<String, Object> environment;

    public DataFromUser(){
        population = new HashMap<>();
        environment = new HashMap<>();
    }
    public DataFromUser(DataFromUser other){
        this.environment = new HashMap<>(other.getEnvironment());
        this.population = new HashMap<>(other.getPopulation());
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
    public void cleanup(){
        population.clear();
        environment.clear();
    }
}
