package world.rule.action;

import exception.SimulationRunningException;
import data.transfer.object.definition.ActionInfo;
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


public class Replace extends Action{
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
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        Map<String, Property> propertiesToCreate = new HashMap<>();

        if(mode.equals("scratch")){
            for(Map.Entry<String, PropertyDefinition> mapEntry : definitionToCreate.getPropsDef().entrySet()){
                String type = mapEntry.getValue().getType();
                PropertyDefinition definition = mapEntry.getValue();
                propertiesToCreate.put(mapEntry.getKey(), createPropertyByDefault(type, definition));
            }
        } else if(mode.equals("derived")) {
            Entity entityToKill = parameters.getMainEntity();

            for(Map.Entry<String, PropertyDefinition> mapEntry : definitionToCreate.getPropsDef().entrySet()){
                String propertyType = mapEntry.getValue().getType();
                String propertyName = mapEntry.getKey();
                PropertyDefinition definition = mapEntry.getValue();

                // check if the entity to kill has an identical property (in name and type)
                if(entityToKill.getProperties().containsKey(propertyName)){ // entity to kill has this property
                    Property propertyToKill = entityToKill.getPropertyByName(propertyName);
                    if(propertyToKill.getType().equalsIgnoreCase(propertyType)){ // equals by name and type !
                        propertiesToCreate.put(propertyName, propertyToKill); // take its values
                    }
                } else { // else -> create regularly
                    propertiesToCreate.put(propertyName, createPropertyByDefault(propertyType, definition));
                }
            }
        }

        entityToCreate.setProperties(propertiesToCreate);
        return true; // needs to kill main entity
    }

    public Property createPropertyByDefault(String type, PropertyDefinition definition){
        switch (type){
            case "Decimal":{}
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

    @Override
    public ActionInfo getActionInfo() {
        boolean haveSecondEntity=false;
        if(secondEntityInfo!=null)
            haveSecondEntity=true;


        Map<String, Object> moreProp = new HashMap<>();
        moreProp.put("Replace with", entityToCreate);

        return new ActionInfo("Replace", mainEntityName, haveSecondEntity, moreProp);
    }
}
