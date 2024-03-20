package world.property.impl;

import data.transfer.object.run.result.PropertyResultInfo;
import exception.SimulationRunningException;
import world.property.api.FloatPropertyDefinition;
import world.property.api.PropertyDefinition;

import java.util.Random;

public class FloatProperty extends Property {
    Double val;

    public FloatProperty(PropertyDefinition definition){
        super(definition);
        if(definition instanceof FloatPropertyDefinition) {
            if (definition.getRandomlyInitialized())
                val = generateRandomValue((Double)definition.getTopLimit(), (Double)definition.getBottomLimit());
            else
                val = (Double)definition.getInitValue();
        }
    }
    @Override
    public PropertyResultInfo getPropertyResultInfo() {
        return new PropertyResultInfo(getName(), getType(), tickNumThatHasChanged, val);
    }

    @Override
    public String getType() {
        return "Float";
    }

    @Override
    public Double getVal() {
        return val;
    }

    @Override
    public void increase(Object increaseBy) throws SimulationRunningException {
        if(increaseBy instanceof Double)
            if((Double)increaseBy+val<=(Double)definition.getTopLimit())
                val = val+(Double) increaseBy;
            else
                val = (Double)definition.getTopLimit();
        else if(increaseBy instanceof Integer)
            if((Integer)increaseBy+val<=(Integer)definition.getTopLimit())
                val = val+(Integer) increaseBy;
            else
                val = (Double)definition.getTopLimit();
        else {
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
            //throw new IncompatibleType();
        }
    }

    @Override
    public void decrease(Object decreaseBy) throws SimulationRunningException {
        if (decreaseBy instanceof Double)
            if (val - (Double) decreaseBy >= (Double) definition.getBottomLimit())
                val = val - (Double) decreaseBy;
            else
                val = (Double) definition.getBottomLimit();
        else if (decreaseBy instanceof Integer)
            if (val - (Integer) decreaseBy >= (Double) definition.getBottomLimit())
                val = val - (Integer) decreaseBy;
            else
                val = (Double) definition.getBottomLimit();
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
        //throw new IncompatibleType();
    }

    @Override
    public void set(Object setTo) throws SimulationRunningException {
        if(setTo instanceof Double)
            if((Double) setTo>=(Double)definition.getBottomLimit() && (Double) setTo<=(Double)definition.getTopLimit())
                val = (Double) setTo;
            else if((Double) setTo<(Double)definition.getBottomLimit())
                val = (Double)definition.getBottomLimit();
            else if ((Double) setTo>(Double)definition.getTopLimit())
                val = (Double)definition.getTopLimit();
            else
                throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
        //throw new IncompatibleType();
    }

    @Override
    public Boolean isBigger(Object toCompere) throws SimulationRunningException {
        if(toCompere instanceof Double)
            return val>(Double) toCompere;
        if(toCompere instanceof Integer)
            return val>(Integer) toCompere;
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
        //throw new IncompatibleType();
    }

    @Override
    public Boolean isSmaller(Object toCompere) throws SimulationRunningException {
        if(toCompere instanceof Double)
            return val<(Double) toCompere;
        else if(toCompere instanceof Integer)
            return val<(Integer) toCompere;
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
        //throw new IncompatibleType();
    }

    private Double generateRandomValue(Double topLimit, Double bottomLimit){
        Random random = new Random();
        return bottomLimit + random.nextFloat() * (topLimit - bottomLimit);
    }
}
