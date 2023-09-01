package world.rule.action;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;


public class Increase extends Action {
    public Increase(String mainEntity, String propToChangeName, String expression) {
        super(mainEntity, propToChangeName, expression);
    }
    @Override
    public Boolean activate(PropertiesToAction propsToChange, int ticks) throws IncompatibleAction, IncompatibleType {
        propsToChange.getMainProp().increase(expressionVal);
        propsToChange.getMainProp().setTicksNotChanged(0);  // update that the property has been changed
        propsToChange.getMainProp().setTickNumThatHasChanged(ticks); // update in which tick it has been changed
        return false;
    }
}
