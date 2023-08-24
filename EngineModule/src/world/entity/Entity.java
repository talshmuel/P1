package world.entity;
import world.property.impl.Property;

import java.io.Serializable;
import java.util.Map;

public class Entity implements Serializable {

    String name;
    Map <String, Property> properties;

    public Entity(String name, Map <String, Property> properties){
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }
    public Property getPropertyByName(String propName){
        if(propName==null)
            return null;
        return properties.get(propName);
    }

}
