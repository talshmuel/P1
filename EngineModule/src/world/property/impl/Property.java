package world.property.impl;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.property.api.PropertyDefinition;

import java.io.Serializable;


public abstract class Property implements Serializable {
    PropertyDefinition definition;
    //String name;

    public Property(PropertyDefinition definition){
        this.definition = definition;
    }

    public abstract String getType();
    public String getName() {
        return definition.getName();
    }

    public abstract Object getVal();
    public abstract void increase(Object increaseBy)throws IncompatibleAction, IncompatibleType;
    public abstract void decrease(Object decreaseBy)throws IncompatibleAction, IncompatibleType ;
    public abstract void set(Object setTo)throws IncompatibleType;
    public abstract Boolean isBigger(Object toCompere)throws IncompatibleAction, IncompatibleType;
    public abstract Boolean isSmaller(Object toCompere)throws IncompatibleAction, IncompatibleType;

    public PropertyDefinition getDefinition() {
        return definition;
    }
}
