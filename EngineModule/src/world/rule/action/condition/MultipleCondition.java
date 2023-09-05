package world.rule.action.condition;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.*;

import java.util.ArrayList;

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
    public Boolean checkCondition(ParametersForAction parameters)throws IncompatibleAction, IncompatibleType {
        switch (logicSign) {
            case OR:
                return checkORCondition((ParametersForMultipleCondition) parameters);
            case AND: {
                return checkANDCondition((ParametersForMultipleCondition) parameters);
            }
        }
        return false;
        /*switch (logicSign) { // old version
            case OR:
                return checkORCondition((PropertiesToMultipleCondition) propsToChange);
            case AND: {
                return checkANDCondition((PropertiesToMultipleCondition) propsToChange);
            }
        }
        return false;*/
    }
    @Override
    public Boolean activate(ParametersForAction parameters)throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if (checkCondition(parameters)) {
            return activateThenActions(((ParametersForMultipleCondition)parameters).getThenParams());
        }
        else if (elseActions != null)
            return activateElseActions(((ParametersForMultipleCondition)parameters).getElseParams());
        else {
            return false;
        }
        /*if (checkCondition(propsToChange)) { // old version
            return activateThenActions(((PropertiesToMultipleCondition)propsToChange).getThenProps());
        }
        else if (elseActions != null)
            return activateElseActions(((PropertiesToMultipleCondition)propsToChange).getElseProps());
        else {
            return false;
        }*/
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
    public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
        /* old version: parameter to method:  ArrayList<PropertiesToAction> props
        boolean kill=false; // old version
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(null, props.get(i)))
                kill = true;
        }
        return kill;*/
    }
    @Override
    public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException ,IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = elseActions.size();
        for(int i=0; i<len;i++){
            if(elseActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
        /* old version: parameter to method:  ArrayList<PropertiesToAction> props
        boolean kill=false; // old version
        int len = elseActions.size();
        for(int i=0; i<len;i++){
            if(elseActions.get(i).activate(null, props.get(i)))
                kill = true;
        }
        return kill;*/
    }
    Boolean checkORCondition(ParametersForMultipleCondition parameters) throws IncompatibleAction, IncompatibleType{
        boolean res = false;
        int len = conditions.size();
        for(int i=0; i<len; i++){
            if(conditions.get(i).checkCondition(parameters.getConditionsParams().get(i)))
                res = true;
        }
        return res;
        /*boolean res = false; // old version
        int len = conditions.size();
        for(int i=0; i<len; i++){
            if(conditions.get(i).checkCondition(props.getConditionsProp().get(i)))
                res = true;
        }
        return res;*/
    }
    Boolean checkANDCondition(ParametersForMultipleCondition parameters) throws IncompatibleAction, IncompatibleType{
        boolean res = true;
        int len = conditions.size();
        for(int i=0; i<len; i++){
            if(!conditions.get(i).checkCondition(parameters.getConditionsParams().get(i)))
                res = false;
        }
        return res;
        /*boolean res = true; // old version
        int len = conditions.size();
        for(int i=0; i<len; i++){
            if(!conditions.get(i).checkCondition(props.getConditionsProp().get(i)))
                res = false;
        }
        return res;*/
    }
}
