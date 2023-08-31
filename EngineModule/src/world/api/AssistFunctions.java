package world.api;
import world.entity.Entity;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class AssistFunctions implements AssistFunctionsInterface, Serializable {
    Map<String, Property> environmentVariables;
    ArrayList<Entity> entities; // todo -> figure out how to use it

    public void setEnvironmentVariables(Map<String, Property> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public Object environment(String propName) {
        return environmentVariables.get(propName).getVal();
    }

    @Override
    public Object random(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    @Override
    public Object evaluate(String expression) {
        // example: evaluate(ent-2.p1)
        String[] splitExpression = expression.split("\\.");
        String entityName = splitExpression[0];
        String propertyName = splitExpression[1];

        for(Entity e : entities){
            if(e.getName().equals(entityName)){
                return e.getPropertyByName(propertyName).getVal();
            }
        }
        return null;
    }

    @Override
    public double percent(double whole, double part) {
        return (part / 100) * whole;
    }

    @Override
    public int ticks(String expression) {
        return 0; // todo
    }
}
