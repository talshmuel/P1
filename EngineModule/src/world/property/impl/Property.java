package world.property.impl;
import data.transfer.object.run.result.PropertyResultInfo;
import exception.SimulationRunningException;
import world.property.api.PropertyDefinition;

import java.io.Serializable;


public abstract class Property implements Serializable {
    PropertyDefinition definition;

    Integer tickNumThatHasChanged; // באיזה טיק הוא השתנה
    //String name;

    public Property(PropertyDefinition definition){
        this.definition = definition;
        this.tickNumThatHasChanged=0;
    }

    public abstract PropertyResultInfo getPropertyResultInfo();
    public abstract String getType();
    public String getName() {
        return definition.getName();
    }
    public abstract Object getVal();
    public abstract void increase(Object increaseBy) throws SimulationRunningException;
    public abstract void decrease(Object decreaseBy) throws SimulationRunningException;
    public abstract void set(Object setTo) throws SimulationRunningException;
    public abstract Boolean isBigger(Object toCompere) throws SimulationRunningException;
    public abstract Boolean isSmaller(Object toCompere) throws SimulationRunningException;
    public PropertyDefinition getDefinition() {
        return definition;
    }



    public Integer getTickNumThatHasChanged() {
        return tickNumThatHasChanged;
    }

    public void setTickNumThatHasChanged(Integer tickNumThatHasChanged) {
        this.tickNumThatHasChanged = tickNumThatHasChanged;
    }


}
