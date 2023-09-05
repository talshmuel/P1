package world.rule.action.condition;
import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;

public abstract class Condition extends Action {
    public Condition(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);
    }
    abstract Boolean checkCondition(ParametersForAction parameters) throws IncompatibleAction, IncompatibleType;
    abstract public ArrayList<Action> getThenActions();
    abstract public ArrayList<Action> getElseActions();
    abstract public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType;
    abstract public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType;
}
