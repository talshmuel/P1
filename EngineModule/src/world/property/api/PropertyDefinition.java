package world.property.api;

import java.io.Serializable;

public abstract class PropertyDefinition implements Serializable {
    String name;
    Boolean isRandomlyInitialized;
    public abstract String getType();
    public PropertyDefinition(String name, Boolean isRandomlyInitialized){
        this.name = name;
        this.isRandomlyInitialized = isRandomlyInitialized;
    }
    public String getName() {
        return name;
    }
    public Boolean getRandomlyInitialized() {
        return isRandomlyInitialized;
    }
    public abstract Object getTopLimit ();
    public abstract Object getBottomLimit ();
    public abstract Object getInitValue();
}
