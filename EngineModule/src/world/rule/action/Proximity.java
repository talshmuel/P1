package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.Grid;
import world.entity.Coordinate;
import world.entity.Entity;
import world.property.impl.Property;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.ParametersForCondition;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;
import java.util.List;

public class Proximity extends Action{
    /**
     EXPLANATION:
     mainEntityName -> source entity
     expression -> by

     works similarly to condition, it has a list of action to perform if the condition is true (thenActions).
     if the entities are close to each other, the actions will be performed.
     **/
    ArrayList<Action> thenActions;
    String targetEntityName;
    Coordinate sourcePos; // updated dynamically (in simulation loop)
    Grid grid;

    public Proximity(String sourceEntityName, SecondaryEntity secondEntityInfo, String propToChangeName, Expression depth,
                     ArrayList<Action> thenActions, String targetEntityName, Grid grid) {
        super(sourceEntityName, secondEntityInfo, propToChangeName, depth);
        this.thenActions = thenActions;
        this.targetEntityName = targetEntityName;
        this.grid = grid;
    }

    public ArrayList<Action> getThenActions() {
        return thenActions;
    }

    public void setSourcePos(Coordinate sourcePos) {
        this.sourcePos = sourcePos;
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        // get the surrounding cells of the main entity (source entity)
        List<Coordinate> surroundingCells = grid.findEnvironmentCells(sourcePos, (Integer)expression.getValue());
        Entity[][] entityMatrix = grid.getEntityMatrix();

        // if in one of those cells there is an entity of target type -> perform the actions)
        for(Coordinate c : surroundingCells){
            if(entityMatrix[c.getRow()][c.getCol()] != null){
                if(entityMatrix[c.getRow()][c.getCol()].getName().equals(targetEntityName)){
                    // one of the thenActions contain the target entity-> similar to SecondaryEntity.
                    Entity targetEntity = entityMatrix[c.getRow()][c.getCol()];
                    ArrayList<ParametersForAction> thenParams = ((ParametersForCondition)parameters).getThenParams();
                    for(int i=0 ; i<thenActions.size() ; i++){
                        if(thenActions.get(i).getMainEntityName().equals(targetEntityName)){
                            // edit the parameters for action
                            String propertyName = thenActions.get(i).getPropToChangeName();
                            thenParams.get(i).setMainProp(targetEntity.getPropertyByName(propertyName));
                            thenParams.get(i).setMainEntity(targetEntity);
                        }
                    }
                    return activateThenActions(((ParametersForCondition)parameters).getThenParams());
                }
            }
        }

        return false; // we haven't found any of the target entities
    }

    public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        boolean kill=false;
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }
}
