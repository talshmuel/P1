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
    String secondaryEntityName; // todo maybe delete
    SecondaryEntity secondEntityInfo;
    String propToChangeName;
    String expression;
    protected Object expressionVal;
    int currentTick; // new

    public Action(String mainEntityName, String secondaryEntityName, String propToChangeName, String expression) {
        this.mainEntityName=mainEntityName;
        this.secondaryEntityName=secondaryEntityName;
        this.propToChangeName=propToChangeName;
        this.expression = expression;
    }
    public String getActionName(){
        return (this.getClass().getSimpleName());
    }
    public String getMainEntityName() {
        return mainEntityName;
    }
    public String getSecondaryEntityName() {
        return secondaryEntityName;
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
    abstract public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange)throws DivisionByZeroException, IncompatibleAction, IncompatibleType;//return true if need to kill, return false else
    public String getExpression() {
        return expression;
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
