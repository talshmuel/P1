package world.rule.action;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

public class Decrease extends Action{
    public Decrease(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);
    }

    @Override
    public Boolean activate(ParametersForAction parameters)throws IncompatibleAction, IncompatibleType {
        parameters.getMainProp().decrease(expression.getValue());
        parameters.getMainProp().setTickNumThatHasChanged(parameters.getCurrentTicks()); // update in which tick it has been changed
        //propsToChange.getMainProp().decrease(expressionVal); // old version
        return false;
    }
}
