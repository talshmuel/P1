package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.Grid;
import world.entity.Coordinate;
import world.entity.Entity;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.ParametersForCondition;
import world.rule.action.api.SecondaryEntity;
import java.util.ArrayList;
import java.util.List;

public class Proximity extends Action { // todo: maybe make it as heir of Single Condition.....
    /**
    EXPLANATION:
     the "expression" field of Action will be the depth (a string)

     the "expressionVal" will be the grid itself

     works similarly to condition, it has a list of action to perform if the condition is true.
     so if the entities are close to each other, the actions will be performed.

     Proximity will ALWAYS have a secondary entity, which is the target entity
    **/

    ArrayList<Action> actions;
    Entity secondEntityInstance; // todo maybe delete

    public Proximity(String mainEntity, SecondaryEntity secondEntityInfo, String targetEntityName, String propToChangeName, String depth, ArrayList<Action> actions){
        /*note: main entity is source entity
        note: second entity is target entity*/
        super(mainEntity, secondEntityInfo, propToChangeName, depth); // note: depth is expression
        this.actions = actions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        Coordinate sourcePos = parameters.getMainEntity().getPosition();
        Coordinate targetPos = parameters.getSecondaryEntity().getPosition();

        Grid grid = (Grid) expressionVal;
        List<Coordinate> sourceCoordinates = grid.findEnvironmentCells(sourcePos, Integer.parseInt(expression));

        for(Coordinate c : sourceCoordinates){
            if(c.equals(targetPos)) {
                return activateActionsList(((ParametersForCondition)parameters).getThenParams());
            }
        }

        return false;
    }

    public Boolean activateActionsList(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill = false;
        int len = actions.size();
        for (int i = 0; i < len ; i++) {
            if(actions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }
}
