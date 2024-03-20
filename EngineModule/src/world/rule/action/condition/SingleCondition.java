package world.rule.action.condition;

import exception.SimulationRunningException;
import world.rule.action.Action;
import world.rule.action.api.*;
import java.util.ArrayList;
import data.transfer.object.definition.ActionInfo;
import java.util.HashMap;
import java.util.Map;

public class SingleCondition extends Condition {
    public enum Operator{EQUAL, NOTEQUAL, BIGGERTHAN, LESSTHAN}
    Operator operator;
    ArrayList<Action> thenActions;
    ArrayList<Action> elseActions;
    Expression propertyExpression;

    public SingleCondition(String mainEntity, SecondaryEntity secondEntityInfo, Expression property, Operator operator,
                           Expression expression, ArrayList<Action> thenActions, ArrayList<Action> elseActions) {
        super(mainEntity, secondEntityInfo, null, expression);
        this.elseActions = elseActions;
        this.operator = operator;
        this.thenActions = thenActions;
        this.propertyExpression = property;
    }

    public Expression getPropertyExpression() {
        return propertyExpression;
    }

    public Boolean checkCondition(ParametersForAction parameters) throws SimulationRunningException {
        switch (operator){
            case EQUAL: return propertyExpression.isEqual(expression);
            case NOTEQUAL: return !(propertyExpression.isEqual(expression));
            case LESSTHAN:  return propertyExpression.isSmaller(expression);
            case BIGGERTHAN: return propertyExpression.isBigger(expression);
        }
        return null;
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        if(checkCondition(parameters))
            return activateThenActions(((ParametersForCondition)parameters).getThenParams());
        else if (elseActions!=null)
            return activateElseActions(((ParametersForCondition)parameters).getElseParams());
        else
            return false;
    }
    @Override
    public ArrayList<Action> getElseActions() {
        return elseActions;
    }
    @Override
    public ArrayList<Action> getThenActions() {
        return thenActions;
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

        moreProp.put("Operator", operator);
        moreProp.put("Compere to", propertyExpression.getName());

        return new ActionInfo("Multiple Condition", super.getMainEntityName(), haveSecondEntity, moreProp);
    }
}