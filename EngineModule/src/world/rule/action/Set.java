package world.rule.action;

import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;

public class Set extends Action{
    public Set(String mainEntity, String secondaryEntity, String propToChangeName, String expression) {
        super(mainEntity, secondaryEntity, propToChangeName, expression);
    }

    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange)throws IncompatibleType {
        parameters.getMainProp().set(expressionVal);
        parameters.getMainProp().setTickNumThatHasChanged(currentTick); // update in which tick it has been changed

        //propsToChange.getMainProp().set(expressionVal); // old version
        return false;
    }
}
