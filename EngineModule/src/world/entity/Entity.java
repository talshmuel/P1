package world.entity;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Entity implements Serializable {
    private static int nextId = 1;
    String name;
    int ID;
    Map <String, Property> properties;
    Coordinate position; // describes the position of the entity on the grid

    public Entity(String name, Map <String, Property> properties){
        this.name = name;
        this.properties = properties;
        this.position = null; // at the beginning it's null, and when the simulation starts, it will be randomized
        this.ID = nextId++;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
