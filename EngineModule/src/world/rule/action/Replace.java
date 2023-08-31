package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;

public class Replace extends Action{
    String mode;
    public Replace(String entityToKill, String entityToCreate, String propToChangeName, String expression, String mode) {
        // main entity is the entity to kill
        super(entityToKill, propToChangeName, expression);
        this.mode = mode;
    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(mode.equals("scratch")){
            // todo: לברוא ישות חדשה לגמרי לפי החוקים הרגילים
        }else if(mode.equals("derived")){
            // todo: מייצרים את היישות החדשה כך שאם וככל שיש לה מאפיינים הזהים על פי שמם וסוגם לאלה של היישות שנהרגה – היא לוקחת את ערכם.
        }

        return true; // needs to kill main entity
    }
}
