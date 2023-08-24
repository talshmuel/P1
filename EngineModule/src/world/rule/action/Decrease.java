package world.rule.action;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;

public class Decrease extends Action{
    public Decrease(String mainEntity, String propToChangeName, String expression) {
        super(mainEntity, propToChangeName, expression);

    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange)throws IncompatibleAction, IncompatibleType {
        propsToChange.getMainProp().decrease(expressionVal);
        return false;
    }
}
