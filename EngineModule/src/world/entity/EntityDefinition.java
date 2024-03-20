package world.entity;

import world.property.api.PropertyDefinition;
import java.io.Serializable;
import java.util.Map;

public class EntityDefinition implements Serializable {
    String name;
    int numOfInstances;
    Map<String, PropertyDefinition> propsDef;
    public EntityDefinition(String name, int numOfInstances, Map<String, PropertyDefinition> propsDef){
        this.name = name;
        this.numOfInstances = numOfInstances;
        this.propsDef = propsDef;
    }

    public String getName() {
        return name;
    }

    public int getNumOfInstances() {
        return numOfInstances;
    }

    public Map<String, PropertyDefinition> getPropsDef() {
        return propsDef;
    }

    public void setNumOfInstances(int numOfInstances) {
        this.numOfInstances = numOfInstances;
    }
}
