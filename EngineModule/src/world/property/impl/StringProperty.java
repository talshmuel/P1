package world.property.impl;

import data.transfer.object.run.result.PropertyResultInfo;
import exception.SimulationRunningException;
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
    public PropertyResultInfo getPropertyResultInfo() {
        return new PropertyResultInfo(getName(), getType(), tickNumThatHasChanged, val);
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
    public void increase(Object increaseBy) throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    @Override
    public void decrease(Object decreaseBy) throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    @Override
    public void set(Object setTo) throws SimulationRunningException {
        if(setTo instanceof String)
            val = (String) setTo;
        else
            throw new SimulationRunningException("Simulation Error:\nAttempt was made to enter a value that does not match the variable type");
    }
    @Override
    public Boolean isBigger(Object toCompere)throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
    }

    @Override
    public Boolean isSmaller(Object toCompere)throws SimulationRunningException {
        throw new SimulationRunningException("Simulation Error:\nAttempt was made to perform an Action that does not match the type of the variable");
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
