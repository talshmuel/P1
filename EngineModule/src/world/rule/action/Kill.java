package world.rule.action;

import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

public class Kill extends Action{

    public Kill(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName){
        super(mainEntity, secondEntityInfo, propToChangeName, null);
    }

    @Override
    public Boolean activate(ParametersForAction parameters) {
        return true;
    }
}
