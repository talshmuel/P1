package world.property.impl;

import exception.IncompatibleAction;
import exception.IncompatibleType;
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
    public String getType() {
        return "Boolean";
    }

    @Override
    public Boolean getVal() {
        return val;
    }

    @Override
    public void increase(Object increaseBy) throws IncompatibleAction {
        throw new IncompatibleAction();
    }

    @Override
    public void decrease(Object decreaseBy) throws IncompatibleAction  {
        throw new IncompatibleAction();
    }

    @Override
    public void set(Object setTo) throws IncompatibleType {
        if(setTo instanceof Boolean)
            val = (Boolean) setTo;
        else
            throw new IncompatibleType();
    }

    @Override
    public Boolean isBigger(Object toCompere) throws IncompatibleAction {
        throw new IncompatibleAction();
    }

    @Override
    public Boolean isSmaller(Object toCompere) throws IncompatibleAction {
        throw new IncompatibleAction();
    }


    private Boolean generateRandomValue(){
        Random random = new Random();
        return random.nextBoolean();
    }
}
