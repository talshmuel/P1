package world.rule.action;

import data.transfer.object.definition.ActionInfo;
import exception.SimulationRunningException;
import world.Grid;
import world.entity.Coordinate;
import world.entity.Entity;
import world.rule.action.api.Expression;
import world.rule.action.api.ParametersForAction;
import world.rule.action.api.ParametersForCondition;
import world.rule.action.api.SecondaryEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Boolean activate(ParametersForAction parameters) throws SimulationRunningException {
        // get the surrounding cells of the main entity (source entity)
        List<Coordinate> surroundingCells = grid.findEnvironmentCells(sourcePos, ((Double)(expression.getValue())).intValue());
        Entity[][] entityMatrix = grid.getEntityMatrix();

        // if in one of those cells there is an entity of target type -> perform the actions)
        for(Coordinate c : surroundingCells){
            if(entityMatrix[c.getRow()][c.getCol()] != null){
                if(entityMatrix[c.getRow()][c.getCol()].getName().equals(targetEntityName)){
                    // one of the thenActions contain the target entity-> similar to SecondaryEntity.
                    Entity targetEntity = entityMatrix[c.getRow()][c.getCol()];
                    ArrayList<ParametersForAction> thenParams = ((ParametersForCondition)parameters).getThenParams();
                    for(int i=0 ; i<thenActions.size() ; i++){
                        if(thenActions.get(i).getMainEntityName().equals(targetEntityName)) {
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

    public Boolean activateThenActions(ArrayList<ParametersForAction> parameters) throws SimulationRunningException {
        boolean kill=false;
        int len = thenActions.size();
        for(int i=0; i<len;i++){
            if(thenActions.get(i).activate(parameters.get(i)))
                kill = true;
        }
        return kill;
    }

    @Override
    public ActionInfo getActionInfo() {
        boolean haveSecondEntity=false;
        if(secondEntityInfo!=null)
            haveSecondEntity=true;


        Map<String, Object> moreProp = new HashMap<>();
        moreProp.put("Source entity", mainEntityName);
        moreProp.put("Target entity", targetEntityName);
        moreProp.put("Environment depth", expression.getName());
        if(thenActions != null)
            moreProp.put("Number of then actions", thenActions.size());

        return new ActionInfo("Proximity", mainEntityName, haveSecondEntity, moreProp);
    }
}
