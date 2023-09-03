package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.World;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Replace extends Action{
    String mode;
    String entityToCreateName;
    public Replace(String entityToKill, SecondaryEntity secondEntityInfo, String entityToCreateName, String mode) {
        // note: main entity is the entity to kill
        // note: secondary entity is the entity to create
        super(entityToKill, secondEntityInfo, null, null);
        this.entityToCreateName = entityToCreateName;
        this.mode = mode;
    }

    /*
    Property mainProp;
    Entity mainEntity; // instance
    Entity secondaryEntity; // instance, if there isn't -> it's null
    int currentTicks;
     */
    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(mode.equals("scratch")){ // todo: לברוא ישות חדשה לגמרי לפי החוקים הרגילים
            Entity toKill = parameters.getMainEntity();
            /*
            String name;
            Map <String, Property> properties;
            Coordinate position;
             */



        } else if(mode.equals("derived")){
            // todo: מייצרים את היישות החדשה כך שאם וככל שיש לה מאפיינים הזהים על פי שמם וסוגם לאלה של היישות שנהרגה – היא לוקחת את ערכם.
        }
        return true; // needs to kill main entity
    }
}
