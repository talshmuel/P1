package world.rule.action;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;

public class Decrease extends Action{
    public Decrease(String mainEntity, String secondaryEntity, String propToChangeName, String expression) {
        super(mainEntity, secondaryEntity, propToChangeName, expression);
    }

    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange)throws IncompatibleAction, IncompatibleType {
        propsToChange.getMainProp().decrease(expressionVal);
        propsToChange.getMainProp().setTickNumThatHasChanged(currentTick); // update in which tick it has been changed
        return false;
    }
}
