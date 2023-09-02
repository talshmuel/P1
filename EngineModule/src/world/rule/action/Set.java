package world.rule.action;

import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

public class Set extends Action{
    public Set(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, String expression) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);
    }

    @Override
    public Boolean activate(ParametersForAction parameters)throws IncompatibleType {
        parameters.getMainProp().set(expressionVal);
        parameters.getMainProp().setTickNumThatHasChanged(currentTick); // update in which tick it has been changed

        //propsToChange.getMainProp().set(expressionVal);
        return false;
    }
}
