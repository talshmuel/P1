package world.property.impl;

import exception.IncompatibleType;
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
    public String getType() {
        return "Integer";
    }

    @Override
    public Integer getVal() {
        return val;
    }

    @Override
    public void increase(Object increaseBy)throws IncompatibleType {
        if(increaseBy instanceof Integer)
            if((Integer)increaseBy+val<=(Integer)definition.getTopLimit())
                val = val+(Integer) increaseBy;
            else
                val = (Integer)definition.getTopLimit();
        else
            throw new IncompatibleType();
    }

    @Override
    public void decrease(Object decreaseBy)throws IncompatibleType {
        if(decreaseBy instanceof Integer)
            if(val-(Integer)decreaseBy>=(Integer)definition.getBottomLimit())
                val = val-(Integer) decreaseBy;
            else
                val = (Integer)definition.getBottomLimit();
        else
            throw new IncompatibleType();

    }

    @Override
    public void set(Object setTo)throws IncompatibleType {
        if(setTo instanceof Integer)
            if((Integer) setTo>=(Integer)definition.getBottomLimit() && (Integer) setTo<=(Integer)definition.getTopLimit())
                val = (Integer) setTo;
            else if ((Integer) setTo<(Integer)definition.getBottomLimit())
                val = (Integer)definition.getBottomLimit();
            else if ((Integer) setTo>(Integer)definition.getTopLimit()) {
                val = (Integer)definition.getTopLimit();
            }
            else
                throw new IncompatibleType();
    }
    @Override
    public Boolean isBigger(Object toCompere)throws IncompatibleType {
        if(toCompere instanceof Integer)
            return val>(Integer) toCompere;
        else if(toCompere instanceof Double)
            return val>(Double) toCompere;
        else
            throw new IncompatibleType();
    }

    @Override
    public Boolean isSmaller(Object toCompere)throws IncompatibleType {
        if(toCompere instanceof Integer)
            return val<(Integer) toCompere;
        else if(toCompere instanceof Double)
            return val<(Double) toCompere;
        else
            throw new IncompatibleType();
    }
    private Integer generateRandomValue(Integer topLimit, Integer bottomLimit){
        Random random = new Random();
        return random.nextInt(topLimit - bottomLimit + 1) + bottomLimit;
    }
}

