package world.rule.action.condition;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.Action;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.PropertiesToCondition;

import java.util.ArrayList;


public class SingleCondition extends Condition {
    public enum Operator{EQUAL, NOTEQUAL, BIGGERTHAN, LESSTHAN}
    Operator operator;
    ArrayList<Action> thenActions;
    ArrayList<Action> elseActions;

    public SingleCondition(String mainEntity, String propToChangeName, Operator operator,
                           String expression, ArrayList<Action> thenActions, ArrayList<Action> elseActions) {
        super(mainEntity, propToChangeName, expression);
        this.elseActions = elseActions;
        this.operator = operator;
        this.thenActions = thenActions;

    }


    public Boolean checkCondition(PropertiesToAction propsToChange)throws IncompatibleAction, IncompatibleType {
        switch (operator){
            case EQUAL: return propsToChange.getMainProp().getVal().equals(expressionVal);
            case NOTEQUAL: return !(propsToChange.getMainProp().getVal().equals(expressionVal));
            case LESSTHAN:  return propsToChange.getMainProp().isSmaller(expressionVal);
            case BIGGERTHAN: return propsToChange.getMainProp().isBigger(expressionVal);
        }
        return null;

    }

    @Override
    public Boolean activate(PropertiesToAction propsToChange, int ticks)throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        if(checkCondition(propsToChange))
            return activateThenActions(((PropertiesToCondition)propsToChange).getThenProps(), ticks);
        else if (elseActions!=null)
            return activateElseActions(((PropertiesToCondition)propsToChange).getElseProps(), ticks);
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
    public Boolean activateThenActions(ArrayList<PropertiesToAction> props, int ticks)throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(props.get(i), ticks))
                kill = true;
        }
        return kill;
    }

    @Override
    public Boolean activateElseActions(ArrayList<PropertiesToAction> props, int ticks)throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = elseActions.size();
        for(int i=0; i<len;i++){
            if(elseActions.get(i).activate(props.get(i), ticks))
                kill = true;
        }
        return kill;
    }
}
