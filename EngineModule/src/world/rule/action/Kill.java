package world.rule.action;

import world.rule.action.api.PropertiesToAction;


public class Kill extends Action{

    public Kill(String mainEntity, String propToChangeName){
        super(mainEntity, propToChangeName, null);
    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange, int ticks) {
        return true;
    }
}
