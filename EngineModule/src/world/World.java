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

import java.io.Serializable;
import java.util.*;

public class World implements Serializable {
    ArrayList<EntityDefinition> entitiesDefinition; // size as number of entities TYPES. describes each entity
    ArrayList<Entity> entities; // todo: delete
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
        this.entities = new ArrayList<>(); // todo: delete later
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

        int i=1;
        for (Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
            System.out.println("-----------------------------------------------");
            System.out.println("Entity #" + i++ + ": " + entry.getKey());
            ArrayList<Entity> instancesList = entry.getValue();
            int j=1;
            for(Entity entity : instancesList){
                System.out.println("    Instance #" + j++ + ":");
                System.out.println("    Name:" + entity.getName());
                System.out.println("    Position: " + entity.getPosition());
                System.out.println("    Properties: ");
                int k=1;
                for (Map.Entry<String, Property> propMap : entity.getProperties().entrySet()){
                    Property p = propMap.getValue();
                    System.out.println("        Property #" + k++ + ":");
                    System.out.println("        Name: " + propMap.getKey());
                    System.out.println("        Name: " + p.getName());
                    System.out.println("        Type: " + p.getType());
                    System.out.println("        Value: " + p.getVal() + "\n");
                }
            }
            System.out.println("-----------------------------------------------");
        }
    }


    public void generateEntitiesByDefinitions(){ // todo delete
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

        // new version
        for(Entity entityToKill : entitiesToKill){
            for(Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
                ArrayList<Entity> entityList = entry.getValue();

                for(Entity entity : entityList){
                    if(entity.equals(entityToKill)){
                        grid.updateGridCoordinateIsAvailableMATRIX(entity.getPosition());
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

                for(Entity entity : entityList){
                    if(entity.equals(entityToCreate)){
                        entity.setPosition(grid.findNewAvailableCell());
                        grid.updateGridCoordinateIsTakenMATRIX(entity.getPosition(), entity);
                        entityList.add(entityToCreate);
                        break;
                    }
                }
            }
        }
    }
    public ArrayList<Entity> getEntities() {
        return entities;
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
    public void setEnvironmentValue(String name, Object val) throws IncompatibleType {
        environmentVariables.get(name).set(val);
    }
    public ArrayList<PropertyValueInfo> getEnvironmentValues(){
        ArrayList<PropertyValueInfo> res = new ArrayList<>();
        environmentVariables.forEach((name, property)->{
            res.add(new PropertyValueInfo(name, property.getVal()));});
        return res;
    }
    public int getNumOfEntitiesLeft(String entityName){ // todo: delete
        int count = 0;
        for(Entity entity : entities)
            if(entity.getName().equals(entityName))
                count++;
        return count;
    }
    public int getNumOfEntitiesLeft2(String entityName){ // new version -> map
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
        entities.clear(); // todo: delete
        allEntities.clear();
    }

    public void generateRandomPositionsOnGrid(){ // scatter the entities on the grid randomly
        Random random = new Random();
        int i=1;
        boolean entityInPlace;
        for (Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
            String entityName = entry.getKey();
            ArrayList<Entity> entityList = entry.getValue();

            System.out.println("Entity #" + i++ + ": " + entityName + ":");
            int j=1;
            for(Entity entity : entityList){
                entity.setPosition(grid.findNewAvailableCell());
                System.out.println("Instance #" + j++ + ": new position: (" + entity.getPosition().getRow() + ", " + entity.getPosition().getCol() + ")");
                /*entityInPlace = false;
                while(!entityInPlace){
                    int newRow = random.nextInt(grid.getNumOfRows());
                    int newCol = random.nextInt(grid.getNumOfCols());

                    if (grid.isPositionAvailable(newRow, newCol)) {
                        Coordinate position = new Coordinate(newRow, newCol);
                        e.setPosition(position);
                        System.out.println("Instance #" + j++ + ": new position: (" + e.getPosition().getRow() + ", " + e.getPosition().getCol() + ")");
                        //grid.updateGridCoordinateIsTaken(position); // todo: delete
                        grid.updateGridCoordinateIsTakenMATRIX(position, e);
                        entityInPlace = true;
                    }
                }*/
            }
        }
        System.out.println("--------------------------------------------------------------------------------------");
    }
    public void moveAllEntitiesOnGrid() {
        for(Map.Entry<String, ArrayList<Entity>> entityMap : allEntities.entrySet()){
            ArrayList<Entity> entityList = entityMap.getValue();
            for(Entity e : entityList){
                Coordinate prevPos = e.getPosition();
                Coordinate newPos = grid.moveEntityOnGridMATRIX(e);

                if(!newPos.equals(prevPos)){ // if we moved the entity
                    e.setPosition(newPos); // set its new location
                    //grid.updateGridCoordinateIsAvailable(prevPos); // update that the previous location is free // todo delete
                    //grid.updateGridCoordinateIsTaken(newPos); // update that the new location is now taken // todo delete

                    grid.updateGridCoordinateIsAvailableMATRIX(prevPos); // update that the previous location is free
                    grid.updateGridCoordinateIsTakenMATRIX(newPos, e); // update that the new location is now taken
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

    public void addEntity(Entity entityToAdd){
        for(Map.Entry<String, ArrayList<Entity>> entry : allEntities.entrySet()){
            String name = entry.getKey();
            if(name.equals(entityToAdd.getName())){
                entry.getValue().add(entityToAdd);
            }
        }
    }



    public Grid getGrid() {
        return grid;
    }
}
