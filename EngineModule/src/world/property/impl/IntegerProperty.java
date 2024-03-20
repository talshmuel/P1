package world.property.impl;

import data.transfer.object.run.result.PropertyResultInfo;
import exception.SimulationRunningException;
import world.property.api.IntegerPropertyDefinition;
import world.property.api.PropertyDefinition;

import java.util.Random;

public class IntegerProperty extends Property {

    Integer val;
    public IntegerProperty(PropertyDefinition definition){
        super(definition);
        if(definition instanceof IntegerPropertyDefinition) {
            if (definition.getRandomlyInitialized())
                val = generateRandomValue((Integer)definition.getTopLimit(), (Integer)definition.getBottomLimit());
            else
                val = (Integer)definition.getInitValue();
        }
    }
    @Override
    public PropertyResultInfo getPropertyResultInfo() {
        return new PropertyResultInfo(getName(), getType(), tickNumThatHasChanged, val);
    }
    @Override
    public String getType() {
        return "Integer";
    }

    @Override
    public Integer getVal() {
        return val;
    }

    @Override
    public void increase(Object increaseBy) throws SimulationRunningException {
        if(increaseBy instanceof Integer)
            if((Integer)increaseBy+val<=(Integer)definition.getTopLimit())
                val = val + (Integer) increaseBy;
            else
                val = (Integer) definition.getTopLimit();
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }

    @Override
    public void decrease(Object decreaseBy) throws SimulationRunningException {
        if(decreaseBy instanceof Integer)
            if(val-(Integer)decreaseBy>=(Integer)definition.getBottomLimit())
                val = val-(Integer) decreaseBy;
            else
                val = (Integer)definition.getBottomLimit();
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }

    @Override
    public void set(Object setTo) throws SimulationRunningException {
        if(setTo instanceof Integer)
            if((Integer) setTo >= (Integer) definition.getBottomLimit() && (Integer) setTo <= (Integer)definition.getTopLimit())
                val = (Integer) setTo;
            else if ((Integer) setTo < (Integer) definition.getBottomLimit())
                val = (Integer) definition.getBottomLimit();
            else if ((Integer) setTo > (Integer) definition.getTopLimit()) {
                val = (Integer) definition.getTopLimit();
            }
            else
                throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }
    @Override
    public Boolean isBigger(Object toCompere) throws SimulationRunningException {
        if(toCompere instanceof Integer)
            return val > (Integer) toCompere;
        else if(toCompere instanceof Double)
            return val > (Double) toCompere;
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }

    @Override
    public Boolean isSmaller(Object toCompere) throws SimulationRunningException {
        if(toCompere instanceof Integer)
            return val < (Integer) toCompere;
        else if(toCompere instanceof Double)
            return val < (Double) toCompere;
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }
    private Integer generateRandomValue(Integer topLimit, Integer bottomLimit){
        Random random = new Random();
        return random.nextInt(topLimit - bottomLimit + 1) + bottomLimit;
    }
}

