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
        Map<String, Property> propertiesToCreate = new HashMap<>();

        if(mode.equals("scratch")){
            for(Map.Entry<String, PropertyDefinition> mapEntry : definitionToCreate.getPropsDef().entrySet()){
                String type = mapEntry.getValue().getType();
                PropertyDefinition definition = mapEntry.getValue();
                propertiesToCreate.put(mapEntry.getKey(), createPropertyByDefault(type, definition));
                /*switch (type){
                    case "Decimal":{}
                    case "Float": {
                        propertiesToCreate.put(mapEntry.getKey(), new FloatProperty(mapEntry.getValue()));
                        break;
                    }
                    case "Boolean": {
                        propertiesToCreate.put(mapEntry.getKey(), new BooleanProperty(mapEntry.getValue()));
                        break;
                    }
                    case "String":{
                        propertiesToCreate.put(mapEntry.getKey(), new StringProperty(mapEntry.getValue()));
                        break;
                    }
                }*/
            }
        } else if(mode.equals("derived")) {
            /**
             *  נעבור על כל אחד מהמאפיינים של הישות שצריך לברוא
             * אם יש מאפיין שזהה בשם ובסוג למאפיין של הישות להרוג -> לקחת את הערכים שלו
             */
            Entity entityToKill = parameters.getMainEntity();

            for(Map.Entry<String, PropertyDefinition> mapEntry : definitionToCreate.getPropsDef().entrySet()){
                String propertyType = mapEntry.getValue().getType();
                String propertyName = mapEntry.getKey();
                PropertyDefinition definition = mapEntry.getValue();

                // check if the entity to kill has an identical property (in name and type)
                Property propertyToKill = entityToKill.getPropertyByName(propertyName);
                if(propertyToKill.getName().equals(propertyName) && propertyToKill.getType().equals(propertyType)){
                    propertiesToCreate.put(propertyName, propertyToKill); // if it does -> take its values
                    // todo: check it doesn't die with the entity
                } else { // else -> create regularly
                    propertiesToCreate.put(propertyName, createPropertyByDefault(propertyType, definition));
                    /*switch (propertyType){
                        case "Decimal":{}
                        case "Float": {
                            propertiesToCreate.put(propertyName, new FloatProperty(d.getValue()));
                            break;
                        }
                        case "Boolean": {
                            propertiesToCreate.put(propertyName, new BooleanProperty(d.getValue()));
                            break;
                        }
                        case "String":{
                            propertiesToCreate.put(propertyName, new StringProperty(d.getValue()));
                            break;
                        }
                    }*/
                }
            }
        }

        entityToCreate.setProperties(propertiesToCreate);
        return true; // needs to kill main entity
    }

    public Property createPropertyByDefault(String type, PropertyDefinition definition){
        switch (type){
            case "Decimal":{} // תקף לשניהם
            case "Float": {
                return new FloatProperty(definition);
            }
            case "Boolean": {
                return new BooleanProperty(definition);
            }
            default: { // case "String"
                return new StringProperty(definition);
            }
        }
    }
}
