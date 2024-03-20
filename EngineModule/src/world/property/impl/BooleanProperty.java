package world.property.impl;

import data.transfer.object.run.result.PropertyResultInfo;
import exception.SimulationRunningException;
import world.property.api.BooleanPropertyDefinition;
import world.property.api.PropertyDefinition;
import java.util.Random;

public class BooleanProperty extends Property {
    Boolean val;

    public BooleanProperty(PropertyDefinition definition){
        super(definition);
        if (definition instanceof BooleanPropertyDefinition) {
            if (definition.getRandomlyInitialized())
                val = generateRandomValue();
            else
                val = (Boolean)definition.getInitValue();
        }
    }

    @Override
    public PropertyResultInfo getPropertyResultInfo() {
        return new PropertyResultInfo(getName(), getType(), tickNumThatHasChanged, val);
    }


    @Override
    public String getType() {
        return "Boolean";
    }

    @Override
    public Boolean getVal() {
        return val;
    }

    @Override
    public void increase(Object increaseBy) throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    @Override
    public void decrease(Object decreaseBy) throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    @Override
    public void set(Object setTo) throws SimulationRunningException {
        if(setTo instanceof Boolean)
            val = (Boolean) setTo;
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }

    @Override
    public Boolean isBigger(Object toCompere) throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    @Override
    public Boolean isSmaller(Object toCompere) throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }


    private Boolean generateRandomValue(){
        Random random = new Random();
        return random.nextBoolean();
    }
}
