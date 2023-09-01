package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;

public class Replace extends Action{
    String mode;
    String entityToCreate;

    public Replace(String entityToKill, String entityToCreate, String mode) {
        // note: main entity is the entity to kill
        super(entityToKill, null, null);
        this.mode = mode;
        this.entityToCreate = entityToCreate;
    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange, int ticks) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(mode.equals("scratch")){

            // todo: לברוא ישות חדשה לגמרי לפי החוקים הרגילים
        }else if(mode.equals("derived")){
            // todo: מייצרים את היישות החדשה כך שאם וככל שיש לה מאפיינים הזהים על פי שמם וסוגם לאלה של היישות שנהרגה – היא לוקחת את ערכם.

        }

        return true; // needs to kill main entity
    }
}
