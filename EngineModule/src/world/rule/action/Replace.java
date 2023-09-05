package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.World;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.property.impl.Property;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Replace extends Action{ // todo של החייםםםםםםםםםםםם
    /**
     * EXPLANATION:
     * in general - the main entity is the one to kill, and the secondary is the one to create
     *
     * mainEntityName = will be the name of the entity to kill
     * secondEntityInfo = will be about the entity to create
     * expression = will be the mode ("scratch" or "derived")
     * expressionVal = will be the INSTANCE of the entity to create!!!
     *
     * in the parameters for action we'll have the instances of both the entity to kill and the entity to create
     * ///////////////////
     *
     * in the XML reading: fill NULL in the EntityInfo section
     * when in the runActions loop: create the instance from the getParametersForAction...
     * **/
    String mode;
    String entityToCreateName;
    public Replace(String entityToKill, SecondaryEntity secondEntityInfo, String entityToCreateName, String mode) {
        // note: main entity is the entity to kill
        // note: secondary entity is the entity to create
        super(entityToKill, secondEntityInfo, null, null);
        this.entityToCreateName = entityToCreateName;
        this.mode = mode;
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(mode.equals("scratch")){ // todo: לברוא ישות חדשה לגמרי לפי החוקים הרגילים
            /* Action:
            -String mainEntityName; = name of entityToKill
            SecondaryEntity secondEntityInfo;  - entityToCreate
            String propToChangeName; = null
            String expression; = mode maybe ????
            protected Object expressionVal;
            int currentTick; */

            /* SecondaryEntity:
            String name; = name of entity to create
            EntityDefinition definition; = definition of entity to create
            Integer numOfSecondEntities; ?
            Condition selection; ?
             */

            /* ParametersForAction:
            Property mainProp;
            Entity mainEntity; // instance
            Entity secondaryEntity; // instance, if there isn't -> it's null
            int currentTicks;
             */

            /**
             * EXPLANATION:
             * mainEntityName = will be the name of the entity to kill
             * secondEntityInfo = will be about the entity to create
             * expression = will be the mode ("scratch" or "derived")
             * expressionVal = will be the INSTANCE of the entity to create!!! (like a return value)
             * in the parameters for action we'll have the instances of both the entity to kill and the entity to create
             * **/

            Entity entityToKill = parameters.getMainEntity();
            Entity entityToCreate = parameters.getSecondaryEntity();
            // need to think, maybe will be like "מימוש ריק "

            Map<String, Property> propertiesToKill = entityToKill.getProperties();
            Map<String, Property> propertiesToCreate = entityToCreate.getProperties();

            for(Map.Entry<String, Property> entry : propertiesToKill.entrySet()){
                String propName = entry.getKey();
                Property property = entry.getValue();
                String propType = property.getType();

                if(propertiesToCreate.containsKey(propName)){
                    Property propToCreate = propertiesToCreate.get(propName);
                    String type = propToCreate.getType();

                    if(propType.equals(type)){
                        System.out.println("property match!");
                        System.out.println("name: " + propToCreate.getName());
                        System.out.println("type: " + propToCreate.getType());
                        System.out.println("value: " + propToCreate.getVal());

                        // create the property....
                    }
                }

            }

            // example:
            // entity1: (p1, property: float) . (p2, property: float) . (p3, property: boolean) . (p4, property: string) .
            // entity2: (p1, property: float) . (p2, property: float)




        } else if(mode.equals("derived")){
            // todo: מייצרים את היישות החדשה כך שאם וככל שיש לה מאפיינים הזהים על פי שמם וסוגם לאלה של היישות שנהרגה – היא לוקחת את ערכם.
        }
        return true; // needs to kill main entity
    }
}
