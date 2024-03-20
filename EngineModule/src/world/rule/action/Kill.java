package world.rule.action;

import data.transfer.object.definition.ActionInfo;
import exception.SimulationRunningException;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;

import java.util.HashMap;
import java.util.Map;

public class Kill extends Action{

    public Kill(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName){
        super(mainEntity, secondEntityInfo, propToChangeName, null);
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        return true;
    }

    @Override
    public ActionInfo getActionInfo() {
        boolean haveSecondEntity=false;
        if(secondEntityInfo!=null)
            haveSecondEntity=true;


        Map<String, Object> moreProp = new HashMap<>();
        moreProp.clear();

        return new ActionInfo("Kill", mainEntityName, haveSecondEntity, moreProp);
    }
}
