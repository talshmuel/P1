package world.rule.action;

import data.transfer.object.definition.ActionInfo;
import exception.SimulationRunningException;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;

import java.util.HashMap;
import java.util.Map;

public class Decrease extends Action{
    public Decrease(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        parameters.getMainProp().decrease(expression.getValue());
        parameters.getMainProp().setTickNumThatHasChanged(parameters.getCurrentTicks()); // update in which tick it has been changed
        //propsToChange.getMainProp().decrease(expressionVal); // old version
        return false;
    }

    @Override
    public ActionInfo getActionInfo() {
        boolean haveSecondEntity=false;
        if(secondEntityInfo!=null)
            haveSecondEntity=true;

        Map<String, Object> moreProp = new HashMap<>();
        moreProp.put("Decrease by", expression.getName());

        return new ActionInfo("Decrease", mainEntityName, haveSecondEntity, moreProp);
    }
}