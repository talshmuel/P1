package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.World;
import world.entity.Entity;
import world.entity.EntityDefinition;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Replace extends Action{
    String mode;
    public Replace(String entityToKill, String secondaryEntity, String mode) {
        // note: main entity is the entity to kill
        // note: second entity is the entity to create
        super(entityToKill, secondaryEntity, null, null);
        this.mode = mode;
    }

    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(mode.equals("scratch")){
            // todo: לברוא ישות חדשה לגמרי לפי החוקים הרגילים
        } else if(mode.equals("derived")){
            // todo: מייצרים את היישות החדשה כך שאם וככל שיש לה מאפיינים הזהים על פי שמם וסוגם לאלה של היישות שנהרגה – היא לוקחת את ערכם.
        }
        return true; // needs to kill main entity
    }
}
