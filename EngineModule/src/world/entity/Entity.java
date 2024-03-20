package world.entity;
import data.transfer.object.run.result.EntityResultInfo;
import data.transfer.object.run.result.PropertyResultInfo;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.HashMap;
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
    public EntityResultInfo getEntityResultInfo(){
        Map<String, PropertyResultInfo> propsResultInfo = new HashMap<>();
        properties.forEach((name, prop)->{
            propsResultInfo.put(name, prop.getPropertyResultInfo());
        });
        return new EntityResultInfo(name, ID,propsResultInfo , position.getRow(), position.getCol());
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

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}
