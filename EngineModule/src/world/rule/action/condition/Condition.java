package world.rule.action.condition;
import exception.SimulationRunningException;
import world.rule.action.Action;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;
import data.transfer.object.definition.ActionInfo;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Condition extends Action implements Serializable {
    public Condition(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName, Expression expression) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);
    }
    abstract Boolean checkCondition(ParametersForAction parameters) throws SimulationRunningException;
    abstract public ArrayList<Action> getThenActions();
    abstract public ArrayList<Action> getElseActions();
    abstract public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws SimulationRunningException;
    abstract public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws SimulationRunningException;

    @Override
    public abstract ActionInfo getActionInfo();
}
