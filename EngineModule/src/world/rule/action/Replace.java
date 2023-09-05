package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.entity.Coordinate;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.property.api.PropertyDefinition;
import world.property.impl.BooleanProperty;
import world.property.impl.FloatProperty;
import world.property.impl.Property;
import world.property.impl.StringProperty;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;

import java.util.HashMap;
import java.util.Map;

public class Replace extends Action{ // todo של החייםםםםםםםםםםםם
    /**
     * EXPLANATION:
     * **/
    String mode;
    String entityToCreateName;
    Entity entityToCreate;
    EntityDefinition definitionToCreate;

    public Replace(String entityToKill, SecondaryEntity secondEntityInfo, String entityToCreateName, String mode, EntityDefinition defToCreate) {
        // note: main entity is the entity to kill
        super(entityToKill, secondEntityInfo, null, null);
        this.entityToCreateName=entityToCreateName;
        this.mode=mode;
        this.definitionToCreate=defToCreate;
    }
    public String getEntityToCreateName() {
        return entityToCreateName;
    }
    public Entity getEntityToCreate() {
        return entityToCreate;
    }
    public void setEntityToCreate(Entity entityToCreate) {
        this.entityToCreate = entityToCreate;
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(mode.equals("scratch")){
            Map <String, Property> properties = new HashMap<>();
            Map<String, PropertyDefinition> propsDef = definitionToCreate.getPropsDef();
            for(Map.Entry<String, PropertyDefinition> d : propsDef.entrySet()){
                String type = d.getValue().getType();
                switch (type){
                    case "Decimal":{}
                    case "Float": {
                        properties.put(d.getKey(), new FloatProperty(d.getValue()));
                        break;
                    }
                    case "Boolean": {
                        properties.put(d.getKey(), new BooleanProperty(d.getValue()));
                        break;
                    }
                    case "String":{
                        properties.put(d.getKey(), new StringProperty(d.getValue()));
                        break;
                    }
                }
            }
            entityToCreate.setProperties(properties);
        } else if(mode.equals("derived")){
            // todo: מייצרים את היישות החדשה כך שאם וככל שיש לה מאפיינים הזהים על פי שמם וסוגם לאלה של היישות שנהרגה – היא לוקחת את ערכם.
        }
        return true; // needs to kill main entity
    }
}
