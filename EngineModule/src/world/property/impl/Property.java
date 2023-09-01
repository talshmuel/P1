package world.property.impl;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.property.api.PropertyDefinition;

import java.io.Serializable;


public abstract class Property implements Serializable {
    PropertyDefinition definition;
    Integer ticksNotChanged; // למשך כמה טיקים ערכו קבוע
    Integer tickNumThatHasChanged; // באיזה טיק הוא השתנה
    //String name;

    public Property(PropertyDefinition definition){
        this.definition = definition;
        this.ticksNotChanged=0;
        this.tickNumThatHasChanged=0;
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
    public Integer getTicksNotChanged() {
        return ticksNotChanged;
    }
    public void setTicksNotChanged(Integer ticksNotChanged) {
        this.ticksNotChanged = ticksNotChanged;
    }

    public Integer getTickNumThatHasChanged() {
        return tickNumThatHasChanged;
    }

    public void setTickNumThatHasChanged(Integer tickNumThatHasChanged) {
        this.tickNumThatHasChanged = tickNumThatHasChanged;
    }

    public void increaseTicksNotChangedByOne(){
        this.ticksNotChanged++;
    }
}
