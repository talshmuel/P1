package world.rule.action;
import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.PropertiesToAction;

import java.io.Serializable;


abstract public class Action implements Serializable {
    String mainEntity;
    String propToChangeName;
    String expression;
    protected Object expressionVal;

    public Action(String mainEntity, String propToChangeName, String expression) {
        this.mainEntity=mainEntity;
        this.propToChangeName=propToChangeName;
        this.expression = expression;
    }

    public String getActionName(){
        return (this.getClass().getSimpleName());
    }

    public String getMainEntity() {
        return mainEntity;
    }

    public String getPropToChangeName() {
        return propToChangeName;
    }


    abstract public Boolean activate(PropertiesToAction propsToChange)throws DivisionByZeroException, IncompatibleAction, IncompatibleType;//return true if need to kill, return false else

    public String getExpression() {
        return expression;
    }

    public void setExpressionVal(Object expressionVal) {
        this.expressionVal = expressionVal;
    }
}
