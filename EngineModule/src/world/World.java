package world;


import data.transfer.object.definition.PropertyValueInfo;
import exception.IncompatibleType;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.property.api.PropertyDefinition;
import world.property.impl.*;
import world.rule.Rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World implements Serializable {
    ArrayList<EntityDefinition> entitiesDefinition;
    ArrayList<Entity> entities;
    Map<String, Property> environmentVariables;
    ArrayList<Rule> rules;
    //ArrayList<EndCondition> endConditions;
    Map<String, Integer> endConditions;

    public Map<String, Property> getEnvironmentVariables() {
        return environmentVariables;
    }

    public World(ArrayList<EntityDefinition> entitiesDefinition, Map<String, Property> environmentVariables,
                 ArrayList<Rule> rules, Map<String, Integer> endConditions){
        this.rules = rules;
        this.environmentVariables = environmentVariables;
        this.entitiesDefinition = entitiesDefinition;
        this.endConditions = endConditions;
        this.entities = new ArrayList<>();
    }

    public void generateEntitiesByDefinitions(){
        for(EntityDefinition entityDef: entitiesDefinition) {
            for (int i = 0; i <entityDef.getNumOfInstances();i++){
                Map <String, Property> entityProps = new HashMap<>();
                for(PropertyDefinition propDef : entityDef.getPropsDef().values()){
                    entityProps.put(propDef.getName(), generatePropertyByDefinitions(propDef));
                }
                entities.add(new Entity(entityDef.getName(),entityProps));
            }
        }
    }
    private Property generatePropertyByDefinitions(PropertyDefinition propdef){
        switch (propdef.getType()){
            case "Boolean":
                return new BooleanProperty(propdef);
            case "Float":
                return new FloatProperty(propdef);
            case "Integer":
                return new IntegerProperty(propdef);
            case "String":
                return new StringProperty(propdef);
        }
        return null;
    }

    public void killEntities(ArrayList<Entity> entitiesToKill){
        for(Entity entity : entitiesToKill){
            entities.remove(entity);
        }
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public final ArrayList<EntityDefinition> getEntitiesDefinition() {
        return entitiesDefinition;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }
    public Integer getEndConditionValueByType(String endConditionType){
        return endConditions.get(endConditionType);
    }

    public ArrayList<PropertyDefinition> getEnvironmentDefinition(){
        ArrayList<PropertyDefinition> res = new ArrayList<>();
        for(Property environmentProp : environmentVariables.values()){
            res.add(environmentProp.getDefinition());
        }
        return res;
    }

    public void setEnvironmentValue(String name, Object val)throws IncompatibleType {
        environmentVariables.get(name).set(val);
    }
    public ArrayList<PropertyValueInfo> getEnvironmentValues(){
        ArrayList<PropertyValueInfo> res = new ArrayList<>();
        environmentVariables.forEach((name, property)->{
            res.add(new PropertyValueInfo(name, property.getVal()));});
        return res;
    }

    public int getNumOfEntitiesLeft(String entityName){
        int count = 0;
        for(Entity entity : entities)
            if(entity.getName().equals(entityName))
                count++;
        return count;
    }

    public void cleanup(){
        entities.clear();
    }
}
