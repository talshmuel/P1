package world.rule.action;


import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;


public class Set extends Action{


    public Set(String mainEntity, String propToChangeName, String expression) {
        super(mainEntity, propToChangeName, expression);

    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange, int ticks)throws IncompatibleType {
        propsToChange.getMainProp().set(expressionVal);
        propsToChange.getMainProp().setTicksNotChanged(0);  // update that the property has been changed
        propsToChange.getMainProp().setTickNumThatHasChanged(ticks); // update in which tick it has been changed
        return false;
    }


}
