package world.rule.action.condition;

import exception.SimulationRunningException;
import world.rule.action.Action;
import world.rule.action.api.*;
import java.util.ArrayList;
import data.transfer.object.definition.ActionInfo;
import java.util.HashMap;
import java.util.Map;

public class MultipleCondition extends Condition {
    ArrayList<Condition> conditions;
    ArrayList<Action> thenActions;
    ArrayList<Action> elseActions;
    public enum Logic {OR, AND}
    Logic logicSign;
    public MultipleCondition(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName,
                             ArrayList<Action> thenActions, ArrayList<Action> elseActions,
                             Logic logicSign, ArrayList<Condition> conditions){
        super(mainEntity, secondEntityInfo, propToChangeName, null);
        this.conditions = conditions;
        this.elseActions = elseActions;
        this.thenActions = thenActions;
        this.logicSign = logicSign;
    }
    public Boolean checkCondition(ParametersForAction parameters) throws SimulationRunningException {
        switch (logicSign) {
            case OR:
                return checkORCondition((ParametersForMultipleCondition) parameters);
            case AND: {
                return checkANDCondition((ParametersForMultipleCondition) parameters);
            }
        }
        return false;
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        if (checkCondition(parameters)) {
            return activateThenActions(((ParametersForMultipleCondition)parameters).getThenParams());
        }
        else if (elseActions != null)
            return activateElseActions(((ParametersForMultipleCondition)parameters).getElseParams());
        else {
            return false;
        }
    }
    @Override
    public ArrayList<Action> getThenActions() {
        return thenActions;
    }
    @Override
    public ArrayList<Action> getElseActions() {
        return elseActions;
    }
    public ArrayList<Condition> getConditions() {
        return conditions;
    }

    public Logic getLogicSign() {
        return logicSign;
    }

    @Override
    public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws SimulationRunningException {
        boolean kill=false;
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }
    @Override
    public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws SimulationRunningException {
        boolean kill=false;
        int len = elseActions.size();
        for(int i=0; i<len;i++){
            if(elseActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }
    Boolean checkORCondition(ParametersForMultipleCondition parameters) throws SimulationRunningException {
        boolean res = false;
        int len = conditions.size();
        for(int i=0; i<len; i++){
            if(conditions.get(i).checkCondition(parameters.getConditionsParams().get(i)))
                res = true;
        }
        return res;
    }
    Boolean checkANDCondition(ParametersForMultipleCondition parameters) throws SimulationRunningException {
        boolean res = true;
        int len = conditions.size();
        for(int i=0; i<len; i++){
            if(!conditions.get(i).checkCondition(parameters.getConditionsParams().get(i)))
                res = false;
        }
        return res;
    }

    @Override
    public ActionInfo getActionInfo() {
        boolean haveSecondEntity=false;
        if(super.getSecondEntityInfo()!=null)
            haveSecondEntity=true;


        Map<String, Object> moreProp = new HashMap<>();
        moreProp.put("Number of then actions", thenActions.size());
        if(elseActions != null){
            moreProp.put("Number of else actions", elseActions.size());
        }

        moreProp.put("Logic", logicSign);
        moreProp.put("Number of conditions", conditions.size());


        return new ActionInfo("Multiple Condition", super.getMainEntityName(), haveSecondEntity, moreProp);
    }
}