package world;


import data.transfer.object.definition.PropertyValueInfo;
import exception.IncompatibleType;
import world.entity.Coordinate;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.property.api.PropertyDefinition;
import world.property.impl.*;
import world.rule.Rule;
import world.rule.action.Action;
import world.rule.action.condition.Condition;

import java.io.Serializable;
import java.util.*;

public class World implements Serializable {
    ArrayList<EntityDefinition> entitiesDefinition; // size as number of entities TYPES. describes each entity
    ArrayList<Entity> entities;
    Map<String, Property> environmentVariables;
    ArrayList<Rule> rules;
    //ArrayList<EndCondition> endConditions;
    Map<String, Integer> endConditions;

    //////// new:
    Map<String, ArrayList<Entity>> allEntities; // new: map by string - name of entity TYPE, to an array of the entity INSTANCES
    Grid grid;
    //Integer numOfThreads;

    public void printMatrix(){ // todo: delete later
        for (int i = 0; i < grid.getNumOfRows(); i++) {
            for (int j = 0; j < grid.getNumOfCols(); j++) {
                System.out.print(grid.matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------");
    }

    public Map<String, Property> getEnvironmentVariables() {
        return environmentVariables;
    }



    public World(ArrayList<EntityDefinition> entitiesDefinition, Map<String, Property> environmentVariables,
                 ArrayList<Rule> rules, Map<String, Integer> endConditions, Grid grid){
        this.rules = rules;
        this.environmentVariables = environmentVariables;
        this.entitiesDefinition = entitiesDefinition;
        this.endConditions = endConditions;
        this.entities = new ArrayList<>();
        this.allEntities = new HashMap<>();
        this.grid = grid;
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

    public void setEnvironmentValue(String name, Object val) throws IncompatibleType {
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

    public void setEntitiesPopulation(String entityName, Integer amount){
        for(EntityDefinition entityDefinition : entitiesDefinition){
            if(entityDefinition.getName().equals(entityName)){
                entityDefinition.setNumOfInstances(amount);
            }
        }
    }
    public void cleanup(){
        entities.clear();
    }

    public void generateRandomPositionsOnGrid(){ // scatter the entities on the grid randomly
        Random random = new Random();

        for(Entity e : entities){
            boolean entityInPlace = false;

            while(!entityInPlace){
                int newRow = random.nextInt(grid.getNumOfRows()-1)+1;
                int newCol = random.nextInt(grid.getNumOfCols()-1)+1;
                if(grid.isPositionAvailable(newRow, newCol)){ // if the place is empty -> place the entity there
                    Coordinate position = new Coordinate(newRow, newCol);
                    //System.out.println("new position is: (" + position.getRow() + ", " + position.getCol() + ")");
                    e.setPosition(position);
                    grid.updateGrid(position);
                    entityInPlace=true;
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

    public void moveAllEntitiesOnGrid() {
        for(Entity e : entities){
            e.setPosition(grid.moveEntityOnGrid(e));
            //System.out.println("DEBUG: new position is: (" + e.getPosition().getRow() + ", " + e.getPosition().getCol() + ")");
        }
    }

    public Grid getGrid() {
        return grid;
    }




}
