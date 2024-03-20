package world;

import data.transfer.object.definition.PropertyValueInfo;
import data.transfer.object.run.result.EntityResultInfo;
import data.transfer.object.run.result.WorldResultInfo;
import exception.SimulationRunningException;
import world.entity.Coordinate;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.property.api.PropertyDefinition;
import world.property.impl.*;
import world.rule.Rule;
import world.rule.action.Action;

import java.io.*;
import java.util.*;

public class World implements Serializable {
    ArrayList<EntityDefinition> entitiesDefinition; // size as number of entities TYPES. describes each entity
    Map<String, Property> environmentVariables;
    ArrayList<Rule> rules;
    Map<String, Integer> endConditions;
    Map<String, ArrayList<Entity>> allEntities; // new: map by string - name of entity TYPE, to an array of the entity INSTANCES
    Grid grid;
    Integer numOfThreads;

    public Integer getNumOfThreads() {
        return numOfThreads;
    }

    public World deepCopy() throws IOException, ClassNotFoundException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        return (World) in.readObject();

    }
    public WorldResultInfo getWorldResultInfo(){
        return new WorldResultInfo(getEntitiesResultInfo(), grid.getGridResultInfo());
    }

    private Map<String, ArrayList<EntityResultInfo>> getEntitiesResultInfo(){
        Map<String, ArrayList<EntityResultInfo>> res = new HashMap<>();
        allEntities.forEach((name, entitiesArray)->{
            ArrayList<EntityResultInfo> entitiesResultInfo = new ArrayList<>();
            entitiesArray.forEach((entity)->{
                entitiesResultInfo.add(entity.getEntityResultInfo());
            });
            res.put(name, entitiesResultInfo);
        });
        return res;
    }
    public Grid getGrid() {
        return grid;
    }
    public Map<String, Property> getEnvironmentVariables() {
        return environmentVariables;
    }
    public World(ArrayList<EntityDefinition> entitiesDefinition, Map<String, Property> environmentVariables,
                 ArrayList<Rule> rules, Map<String, Integer> endConditions, Grid grid, Integer numOfThreads){
        this.rules = rules;
        this.environmentVariables = environmentVariables;
        this.entitiesDefinition = entitiesDefinition;
        this.endConditions = endConditions;
        this.allEntities = new HashMap<>();
        this.grid = grid;
        this.numOfThreads = numOfThreads;
    }

    public void generateAllEntitiesMapByDefinition(){ // adding to the map: the name of entity, and the array list of instances.
        for(EntityDefinition entityDef : entitiesDefinition){
            String entityName = entityDef.getName(); // key
            ArrayList<Entity> instancesList = new ArrayList<>(); // value
            int population = entityDef.getNumOfInstances();
            for(int i=0 ; i < population ; i++){
                Map <String, Property> entityProps = new HashMap<>();
                for(PropertyDefinition propDef : entityDef.getPropsDef().values()){
                    entityProps.put(propDef.getName(), generatePropertyByDefinitions(propDef));
                }
                instancesList.add(new Entity(entityDef.getName(),entityProps));
            }
            allEntities.put(entityName, instancesList);
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
        for(Entity entityToKill : entitiesToKill){
            for(Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
                ArrayList<Entity> entityList = entry.getValue();

                for(Entity entity : entityList){
                    if(entity.equals(entityToKill)){
                        grid.updateGridCoordinateIsAvailable(entity.getPosition());
                        entityList.remove(entity);
                        break;
                    }
                }
            }
        }

    }

    public void createEntities(ArrayList<Entity> entitiesToCreate){
        for(Entity entityToCreate : entitiesToCreate){
            for(Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
                ArrayList<Entity> entityList = entry.getValue();

                for(Entity entity : entityList) {
                    if(entity.equals(entityToCreate)) {
                        entity.setPosition(grid.findNewAvailableCell());
                        grid.updateGridCoordinateIsTaken(entity.getPosition(), entity);
                        entityList.add(entityToCreate);
                        break;
                    }
                }
            }
        }


    }
    public Map<String, Integer> getEntitiesAmount() {
        Map<String, Integer> res = new HashMap<>();
        allEntities.forEach((entName, entInstances) -> {
            res.put(entName, entInstances.size());
        });
        return res;
    }

    public Map<String, ArrayList<Entity>> getAllEntities() {
        return allEntities;
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
    public void setEnvironmentValue(String name, Object val) throws SimulationRunningException {
        try {
            environmentVariables.get(name).set(val);
        } catch (SimulationRunningException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<PropertyValueInfo> getEnvironmentValues(){
        ArrayList<PropertyValueInfo> res = new ArrayList<>();
        environmentVariables.forEach((name, property)->{
            res.add(new PropertyValueInfo(name, property.getVal()));});
        return res;
    }

    public int getNumOfEntitiesLeft(String entityName){ // new version -> map
        int count = 0;
        for(Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()) {
            ArrayList<Entity> entityList = entry.getValue();
            for(Entity entity : entityList){
                if(entity.getName().equals(entityName))
                    count++;
            }
        }
        return count;
    }

    public void setEntitiesPopulation(String entityName, Integer amount){
        for(EntityDefinition entityDefinition : entitiesDefinition){
            if(entityDefinition.getName().equals(entityName)){
                entityDefinition.setNumOfInstances(amount);
            }
        }
    }
    public void cleanup(){
        entitiesDefinition.clear();
        environmentVariables.clear();
        rules.clear();
        endConditions.clear();
        allEntities.clear();
        grid.cleanup();
    }



    public void generateRandomPositionsOnGrid(){ // scatter the entities on the grid randomly
        for (Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
            ArrayList<Entity> entityList = entry.getValue();
            for(Entity entity : entityList){
                entity.setPosition(grid.findNewAvailableCell());
            }
        }
    }
    public void moveAllEntitiesOnGrid() {
        for(Map.Entry<String, ArrayList<Entity>> entityMap : allEntities.entrySet()){
            ArrayList<Entity> entityList = entityMap.getValue();
            for(Entity e : entityList){
                Coordinate prevPos = e.getPosition();
                Coordinate newPos = grid.moveEntityOnGrid(e);

                if(!newPos.equals(prevPos)){ // if we moved the entity
                    e.setPosition(newPos); // set its new location
                    grid.updateGridCoordinateIsAvailable(prevPos); // update that the previous location is free
                    grid.updateGridCoordinateIsTaken(newPos, e); // update that the new location is now taken
                }
            }
        }
    }

    public void generateDefinitionForSecondaryEntity(){
        for(Rule rule : rules){
            for(Action action : rule.getActions()){
                if(action.getSecondEntityInfo() != null){
                    for(EntityDefinition d : entitiesDefinition){
                        if(d.getName().equals(action.getSecondEntityInfo().getName())){
                            EntityDefinition definition = new EntityDefinition(d.getName(), d.getNumOfInstances(), d.getPropsDef());
                            action.getSecondEntityInfo().setDefinition(definition);
                        }
                    }
                }
            }
        }
    }
}
