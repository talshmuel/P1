package world.entity;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.Map;

public class Entity implements Serializable {
    String name;
    Map <String, Property> properties;
    Coordinate position; // describes the position of the entity on the grid

    public Entity(String name, Map <String, Property> properties){
        this.name = name;
        this.properties = properties;
        this.position = null; // at the beginning it's null, and when the simulation starts, it will be randomized
    }

    public String getName() {
        return name;
    }
    public Property getPropertyByName(String propName){
        if(propName==null)
            return null;
        return properties.get(propName);
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }
}
