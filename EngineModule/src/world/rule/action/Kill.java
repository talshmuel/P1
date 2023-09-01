package world.rule.action;

import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;


public class Kill extends Action{

    public Kill(String mainEntity, String secondaryEntity, String propToChangeName){
        super(mainEntity, secondaryEntity, propToChangeName, null);
    }

    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange) {
        return true;
    }
}
