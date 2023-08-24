package world.rule.action;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;


public class Increase extends Action {


    public Increase(String mainEntity, String propToChangeName, String expression) {
        super(mainEntity, propToChangeName, expression);

    }
    @Override
    public Boolean activate(PropertiesToAction propsToChange) throws IncompatibleAction, IncompatibleType {
        propsToChange.getMainProp().increase(expressionVal);
        return false;
    }



}
