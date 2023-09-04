package world.rule.action.condition;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.entity.Coordinate;
import world.rule.action.Action;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;

public class Proximity extends Condition{
    /**
     EXPLANATION:
     the "expression" field of Action will be the depth (a string)
     the "expressionVal" will be the grid itself

     works similarly to condition, it has a list of action to perform if the condition is true (thenActions)
     so if the entities are close to each other, the actions will be performed.

     **/
    ArrayList<Action> thenActions;
    String targetEntityName;
    Coordinate sourcePos;
    Coordinate targetPos;

    public Proximity(String mainEntity, SecondaryEntity secondEntityInfo, String propToChangeName,
                     String expression, ArrayList<Action> thenActions, String targetEntityName) {
        super(mainEntity, secondEntityInfo, propToChangeName, expression);  // defaulted
        this.thenActions = thenActions;
        this.targetEntityName = targetEntityName;
    }

    public void setTargetPos(Coordinate targetPos) {
        this.targetPos = targetPos;
    }

    public void setSourcePos(Coordinate sourcePos) {
        this.sourcePos = sourcePos;
    }

    @Override // defaulted
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        return null;
    }

    @Override // defaulted
    Boolean checkCondition(ParametersForAction parameters) throws IncompatibleAction, IncompatibleType {
        return null;
    }

    @Override // defaulted
    public ArrayList<Action> getThenActions() {
        return null;
    }

    @Override // defaulted
    public ArrayList<Action> getElseActions() {
        return null;
    }

    @Override // defaulted
    public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        return null;
    }

    @Override // defaulted
    public Boolean activateElseActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        return null;
    }
}
