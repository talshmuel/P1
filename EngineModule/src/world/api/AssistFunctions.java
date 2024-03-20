package world.api;
import world.entity.Entity;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class AssistFunctions implements AssistFunctionsInterface, Serializable {
    Map<String, Property> environmentVariables;
    Map<String, ArrayList<Entity>> allEntities;
    Integer numOfTicksInSimulation;

    public void setEnvironmentVariables(Map<String, Property> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void setAllEntities(Map<String, ArrayList<Entity>> allEntities) {
        this.allEntities = allEntities;
    }

    public void setNumOfTicksInSimulation(Integer numOfTicksInSimulation) {
        this.numOfTicksInSimulation = numOfTicksInSimulation;
    }

    @Override
    public Object environment(String propName) {
        return environmentVariables.get(propName).getVal();
    }

    @Override
    public Integer random(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    @Override
    public Object evaluate(String expression, Entity mainEntity, Entity secondEntity, Entity thirdEntity) {
        String[] splitExpression = expression.split("\\.");
        String entityName = splitExpression[0];
        String propertyName = splitExpression[1];

        if(mainEntity.getName().equals(entityName)){ // it's the main entity's property
            return mainEntity.getPropertyByName(propertyName).getVal();
        } else if (secondEntity != null) {
            return secondEntity.getPropertyByName(propertyName).getVal(); // it's the secondary entity's property
        } else if (thirdEntity != null) {
            return thirdEntity.getPropertyByName(propertyName).getVal(); // only in proximity and replace
        } else {
            return mainEntity.getPropertyByName(propertyName).getVal();
        }

    }

    @Override
    public Double percent(String expression, Entity mainEntity, Entity secondEntity, Entity thirdEntity) {
        String[] splitExpression = expression.split(",");
        String whole = splitExpression[0];
        String part = splitExpression[1];

        Double wholeNum = trimPercentExpressionToNumber(whole, mainEntity, secondEntity, thirdEntity);
        Double partNum = trimPercentExpressionToNumber(part, mainEntity, secondEntity, thirdEntity);

        return (partNum/100)*wholeNum;
    }

    public Double trimPercentExpressionToNumber(String whole, Entity mainEntity, Entity secondEntity, Entity thirdEntity){
        if(whole.startsWith("environment")){
            return (Double) environment(whole.substring(12, whole.length()-1));
        } else if(whole.startsWith("random")){
            Integer randomResult = random(Integer.parseInt(whole.substring(7, whole.length()-1)));
            return new Double(randomResult);
        } else if(whole.startsWith("evaluate")){
            return (Double) evaluate(whole.substring(9, whole.length()-1), mainEntity, secondEntity, thirdEntity);
        } else if(whole.startsWith("percent")){
            whole = whole.substring(8, whole.length()-1);
            return percent(whole, mainEntity, secondEntity, thirdEntity);
        } else if(whole.startsWith("ticks")){
            return (double) ticks(whole.substring(6, whole.length()-1), mainEntity);
        } else { // is a number
            return Double.parseDouble(whole);
        }
    }


    /** Explanation: each property has a field of "Integer tickNumThatHasChanged"
     in each action that can change a property value (increase, decrease, multiply, divide, set) we update in which tick the property was changed.
     so in this function we take the number of ticks in the simulation and subtract the property's field of tickNumThatHasChanged.**/
    @Override
    public int ticks(String expression, Entity entity) {
        String[] splitExpression = expression.split("\\.");
        String entityName = splitExpression[0];
        String propertyName = splitExpression[1];

        if(entity.getName().equals(entityName)){
            int tickNumThatHasChanged = entity.getPropertyByName(propertyName).getTickNumThatHasChanged();
            return (this.numOfTicksInSimulation-tickNumThatHasChanged);
        } else {
            int tickNumThatHasChanged = entity.getPropertyByName(propertyName).getTickNumThatHasChanged();
            return (this.numOfTicksInSimulation-tickNumThatHasChanged);
        }
    }
}
