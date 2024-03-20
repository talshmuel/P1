package world.rule.action;

import data.transfer.object.definition.ActionInfo;
import exception.SimulationRunningException;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;
import java.io.Serializable;

abstract public class Action implements Serializable {
    String mainEntityName;
    SecondaryEntity secondEntityInfo; // if there's a second entity, in the xml we fill only name, count and selection
    String propToChangeName;

    protected Expression expression;
    public Action(String mainEntityName, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression) {
        this.mainEntityName=mainEntityName;
        this.secondEntityInfo=secondEntityInfo;
        this.propToChangeName=propToChangeName;
        this.expression=expression;
    }
    public Expression getExpressionNew() {
        return expression;
    }
    public void setExpressionNew(Expression expression) {
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
    abstract public Boolean activate(ParametersForAction parameters) throws SimulationRunningException;//return true if need to kill, return false else
    abstract public ActionInfo getActionInfo();
}
