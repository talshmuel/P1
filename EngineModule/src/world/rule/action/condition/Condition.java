package world.rule.action.condition;
import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.PropertiesToAction;

import java.util.ArrayList;

public abstract class Condition extends Action {
    public Condition(String mainEntity, String propToChangeName, String expression) {
        super(mainEntity, propToChangeName, expression);

    }
    abstract Boolean checkCondition(PropertiesToAction propsToChange)throws IncompatibleAction, IncompatibleType;
    abstract public ArrayList<Action> getThenActions();
    abstract public ArrayList<Action> getElseActions();
    abstract public Boolean activateThenActions(ArrayList<PropertiesToAction> props)throws DivisionByZeroException, IncompatibleAction, IncompatibleType;
    abstract public Boolean activateElseActions(ArrayList<PropertiesToAction> props)throws DivisionByZeroException, IncompatibleAction, IncompatibleType;
}
