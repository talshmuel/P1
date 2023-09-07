package world.rule.action.condition;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.*;

import java.util.ArrayList;

public class SingleCondition extends Condition {
    public enum Operator{EQUAL, NOTEQUAL, BIGGERTHAN, LESSTHAN}
    Operator operator;
    ArrayList<Action> thenActions;
    ArrayList<Action> elseActions;
    Expression propertyExpression; // todo של החיים

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

    public Boolean checkCondition(ParametersForAction parameters) throws IncompatibleAction, IncompatibleType {
        switch (operator){
            case EQUAL: return propertyExpression.isEqual(expression);
            case NOTEQUAL: return !(propertyExpression.isEqual(expression));
            case LESSTHAN:  return propertyExpression.isSmaller(expression);
            case BIGGERTHAN: return propertyExpression.isBigger(expression);
        }
        return null;
    }
    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
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
    public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }
    @Override
    public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = elseActions.size();
        for(int i=0; i<len;i++){
            if(elseActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }

    ///////////////////////////////////////////////////// old versions:
    /*public Boolean checkCondition(ParametersForAction parameters) throws IncompatibleAction, IncompatibleType {
        switch (operator){ // old version
            case EQUAL: return propsToChange.getMainProp().getVal().equals(expressionVal);
            case NOTEQUAL: return !(propsToChange.getMainProp().getVal().equals(expressionVal));
            case LESSTHAN:  return propsToChange.getMainProp().isSmaller(expressionVal);
            case BIGGERTHAN: return propsToChange.getMainProp().isBigger(expressionVal);
        }
        return null;
    }*/

    /*public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(checkCondition(propsToChange))
            return activateThenActions(((PropertiesToCondition)propsToChange).getThenProps());
        else if (elseActions!=null)
            return activateElseActions(((PropertiesToCondition)propsToChange).getElseProps());
        else
            return false;
    }*/

    /*public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false; // old version
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(null, props.get(i)))
                kill = true;
        }
        return kill;
    }*/
    /*public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false; // old version
        int len = elseActions.size();
        for(int i=0; i<len;i++){
            if(elseActions.get(i).activate(null, props.get(i)))
                kill = true;
        }
        return kill;
    }*/

}
