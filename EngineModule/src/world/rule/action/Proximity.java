package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;

public class Proximity extends Action {
    String targetEntity;

    public Proximity(String mainEntity, String targetEntity, String propToChangeName, String depth){
        // note: main entity is source entity
        super(mainEntity, propToChangeName, depth); // note: depth is expression
        // todo: לבדוק מה לעשות לגבי propToChangeName?
        this.targetEntity = targetEntity;
    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange, int ticks) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        // todo
        return false;
    }
}
