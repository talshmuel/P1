package world.rule.action;


import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;


public class Set extends Action{


    public Set(String mainEntity, String propToChangeName, String expression) {
        super(mainEntity, propToChangeName, expression);

    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange)throws IncompatibleType {
        propsToChange.getMainProp().set(expressionVal);
        return false;
    }


}
