package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.api.SecondaryEntity;
import world.rule.action.condition.Condition;

import java.util.ArrayList;

public class Proximity extends Action {
    ArrayList<Action> actions; // works similarly to condition, it has a list of action to perform if the condition is true
    String targetEntityName;

    public Proximity(String mainEntity, SecondaryEntity secondEntityInfo, String targetEntityName, String propToChangeName, String depth, ArrayList<Action> actions){
        // note: main entity is source entity
        // note: second entity is target entity
        super(mainEntity, secondEntityInfo, propToChangeName, depth); // note: depth is expression
        this.targetEntityName = targetEntityName;
        this.actions = actions;
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        // main entity.proximity()
        // in Entity:
        // public Boolean proximity(String targetEntity){
        //
        // }
        // todo
        return false;
    }
}
