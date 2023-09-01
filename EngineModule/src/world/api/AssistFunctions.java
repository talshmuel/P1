package world.api;
import world.entity.Entity;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class AssistFunctions implements AssistFunctionsInterface, Serializable {
    Map<String, Property> environmentVariables;
    ArrayList<Entity> entities;
    Integer numOfTicksInSimulation;

    public void setEnvironmentVariables(Map<String, Property> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
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
    public Object evaluate(String expression) {
        // example: evaluate(ent-1.p2)
        String[] splitExpression = expression.split("\\.");
        String entityName = splitExpression[0];
        String propertyName = splitExpression[1];

        for(Entity e : entities){
            if(e.getName().equals(entityName)){
                return e.getPropertyByName(propertyName).getVal();
            }
        }
        return null; // todo: maybe exception?
    }

//    @Override
//    public Double percent(double whole, double part) {
//        // example: percent(evaluate(ent-2.p1),environment(e1))
//        return (part / 100) * whole;
//    }


    @Override
    public Double percent(String expression) { // todo: check the hell out of this method!!!
        String[] splitExpression = expression.split(",");
        String whole = splitExpression[0];
        String part = splitExpression[1];

        Double wholeNum = trimPercentExpressionToNumber(whole);
        Double partNum = trimPercentExpressionToNumber(part);

        return (partNum/100)*wholeNum;
    }

    public Double trimPercentExpressionToNumber(String whole){
        if(whole.startsWith("environment")){
            return (Double) environment(whole.substring(12, whole.length()-1));
        } else if(whole.startsWith("random")){
            Integer randomResult = random(Integer.parseInt(whole.substring(7, whole.length()-1)));
            return new Double(randomResult);
        } else if(whole.startsWith("evaluate")){
            return (Double) evaluate(whole.substring(9, whole.length()-1));
        } else if(whole.startsWith("percent")){
            whole = whole.substring(8, whole.length()-1);
            return percent(whole);
        } else if(whole.startsWith("ticks")){
            return (double) ticks(whole.substring(6, whole.length()-1));
        } else { // is a number
            return Double.parseDouble(whole);
        }
    }


    /**Explanation: each property has a field of "Integer tickNumThatHasChanged"
     in each action that can change a property value (increase, decrease, multiply, divide, set) we update in which tick the property was changed.
     so in this function we take the number of ticks in the simulation and subtract the property's field of tickNumThatHasChanged.**/
    @Override
    public int ticks(String expression) {
        String[] splitExpression = expression.split("\\.");
        String entityName = splitExpression[0];
        String propertyName = splitExpression[1];

        for(Entity e : entities){
            if(e.getName().equals(entityName)){
                int tickNumThatHasChanged = e.getPropertyByName(propertyName).getTickNumThatHasChanged();
                return (this.numOfTicksInSimulation-tickNumThatHasChanged);
            }
        }
        return 0; // todo: maybe exception?
    }
}
