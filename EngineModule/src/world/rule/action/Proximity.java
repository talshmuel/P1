package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.PropertiesToAction;
import world.rule.action.condition.Condition;

import java.util.ArrayList;

public class Proximity extends Action {
    ArrayList<Action> actions;

    public Proximity(String mainEntity, String secondaryEntity, String propToChangeName, String depth, ArrayList<Action> actions){
        // note: main entity is source entity
        // note: secondary entity is target entity
        super(mainEntity, secondaryEntity, propToChangeName, depth); // note: depth is expression
        this.actions=actions;
    }

    @Override
    public Boolean activate(ParametersForAction parameters, PropertiesToAction propsToChange) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        // main entity.proximity()
        // in Entity:
        // public Boolean proximity(String targetEntity){
        //
        // }
        // todo
        return false;
    }
}
