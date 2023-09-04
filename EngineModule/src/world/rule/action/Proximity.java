package world.rule.action;

import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
import world.Grid;
import world.entity.Coordinate;
import world.entity.Entity;
import world.rule.action.Action;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.ParametersForCondition;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;
import java.util.List;

public class Proximity extends Action{
    /**
     EXPLANATION:
     the "expression" field is the "of"

     works similarly to condition, it has a list of action to perform if the condition is true (thenActions)
     so if the entities are close to each other, the actions will be performed.

     **/
    ArrayList<Action> thenActions;
    String targetEntityName;
    Coordinate sourcePos;
    Grid grid;

    public Proximity(String mainEntityName, SecondaryEntity secondEntityInfo, String propToChangeName, String expression,
                     ArrayList<Action> thenActions, String targetEntityName, Grid grid) {
        super(mainEntityName, secondEntityInfo, propToChangeName, expression);
        this.thenActions = thenActions;
        this.targetEntityName = targetEntityName;
        this.grid = grid;
    }
    // need to add: grid? or maybe just the list of cells


    public ArrayList<Action> getThenActions() {
        return thenActions;
    }

    public void setSourcePos(Coordinate sourcePos) {
        this.sourcePos = sourcePos;
    }

    @Override
    public Boolean activate(ParametersForAction parameters) throws DivisionByZeroException, IncompatibleAction, IncompatibleType {
        // get the surrounding cells of the main entity (source entity)
        List<Coordinate> surroundingCells = grid.findEnvironmentCells(sourcePos, (Integer)expressionVal);
        Entity[][] entityMatrix = grid.getEntityMatrix();

        // if in one of those cells there is an entity of target type -> perform the actions)\
        for(Coordinate c : surroundingCells){
            if(entityMatrix[c.getRow()][c.getCol()] != null){
                if(entityMatrix[c.getRow()][c.getCol()].getName().equals(targetEntityName)){
                    Entity targetEntity = entityMatrix[c.getRow()][c.getCol()];

                    ArrayList<ParametersForAction> thenParams = ((ParametersForCondition)parameters).getThenParams();
                    for(int i=0 ; i<thenActions.size() ; i++){
                        if(thenActions.get(i).getMainEntityName().equals(targetEntityName)){
                            System.out.println("@@@@");
                        }
                    }
                    return activateThenActions(((ParametersForCondition)parameters).getThenParams());
                }
            }
        }

        // we haven't found any of the target entities
        return false;
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
