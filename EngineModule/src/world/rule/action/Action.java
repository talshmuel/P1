package world.rule.action;
import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;

import java.io.Serializable;


abstract public class Action implements Serializable {
    String mainEntityName;
    //String secondEntityName;
    SecondaryEntity secondEntityInfo; // if there's a second entity, in the xml we fill only name, count and selection
    String propToChangeName;
    String expression;
    protected Object expressionVal;
    int currentTick; // todo: maybe can delete because we get it in the ParametersForAction

    public Action(String mainEntityName, SecondaryEntity secondEntityInfo, String propToChangeName, String expression) {
        this.mainEntityName=mainEntityName;
        this.secondEntityInfo=secondEntityInfo;
        this.propToChangeName=propToChangeName;
        this.expression = expression;
    }
    public String getActionName(){
        return (this.getClass().getSimpleName());
    }
    public String getMainEntityName() {
        return mainEntityName;
    }
    public String getPropToChangeName() {
        return propToChangeName;
    }
    public SecondaryEntity getSecondEntityInfo() {
        return secondEntityInfo;
    }
    public void setSecondEntityInfo(SecondaryEntity secondEntityInfo) {
        this.secondEntityInfo = secondEntityInfo;
    }
    abstract public Boolean activate(ParametersForAction parameters)throws DivisionByZeroException, IncompatibleAction, IncompatibleType;//return true if need to kill, return false else
    public String getExpression() {
        return expression;
    }

    public Object getExpressionVal() {
        return expressionVal;
    }

    public void setExpressionVal(Object expressionVal) {
        this.expressionVal = expressionVal;
    }
    public int getCurrentTick() {
        return currentTick;
    }
    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }
}
