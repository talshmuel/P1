package world.rule.action;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;


public class Increase extends Action {
    public Increase(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, String expression) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws IncompatibleAction, IncompatibleType {
        parameters.getMainProp().increase(expressionVal);
        parameters.getMainProp().setTickNumThatHasChanged(currentTick); // update in which tick it has been changed

        //propsToChange.getMainProp().increase(expressionVal); // old version
        return false;
    }
}
