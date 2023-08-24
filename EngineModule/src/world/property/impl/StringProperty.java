package world.property.impl;

import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.property.api.PropertyDefinition;
import world.property.api.StringPropertyDefinition;

import java.util.Random;

public class StringProperty extends Property {
    String val;

    public StringProperty(PropertyDefinition definition){
        super(definition);
        if(definition instanceof StringPropertyDefinition) {
            if (definition.getRandomlyInitialized())
                val = generateRandomValue(((StringPropertyDefinition)definition).getMaxRandomSize());
            else
                val = (String) definition.getInitValue();
        }
    }
    @Override
    public String getType() {
        return "String";
    }

    @Override
    public String getVal() {
        return val;
    }

    @Override
    public void increase(Object increaseBy)throws IncompatibleAction {
        throw new IncompatibleAction();
    }

    @Override
    public void decrease(Object decreaseBy)throws IncompatibleAction {
        throw new IncompatibleAction();
    }

    @Override
    public void set(Object setTo)throws IncompatibleType {
        if(setTo instanceof String)
            val = (String) setTo;
        else
            throw new IncompatibleType();
    }
    @Override
    public Boolean isBigger(Object toCompere)throws IncompatibleAction {
        throw new IncompatibleAction();
    }

    @Override
    public Boolean isSmaller(Object toCompere)throws IncompatibleAction {
        throw new IncompatibleAction();
    }
    private String generateRandomValue(int maxSize){
        Random random = new Random();

        int length = random.nextInt(maxSize) + 1;

        StringBuilder randomString = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789!?,_-.()";

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

}
