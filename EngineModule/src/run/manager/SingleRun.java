package run.manager;


import data.transfer.object.DataFromUser;
import data.transfer.object.run.result.RunResultInfo;
import data.transfer.object.run.result.WorldResultInfo;
import exception.SimulationRunningException;
import world.World;
import world.api.AssistFunctions;
import world.entity.Entity;
import world.property.impl.Property;
import world.rule.Rule;
import world.rule.action.*;
import world.rule.action.api.*;
import world.rule.action.calculation.Calculation;
import world.rule.action.condition.Condition;
import world.rule.action.condition.MultipleCondition;
import world.rule.action.condition.SingleCondition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SingleRun {
    public enum RunState{PENDING, RUNNING, PAUSED, DONE, CANCELLED}
    AssistFunctions functions;
    World world;
    int ticks;
    long startTime;
    int runID;

    long totalRunTime;
    RunState currentState;
    String cancelReason;

    ArrayList<World> worldResultAtEveryTick;


    public SingleRun(World worldDefinition, int runID) throws IOException, ClassNotFoundException {
        this.world = worldDefinition.deepCopy();
        this.runID = runID;
        ticks=0;
        currentState = RunState.PENDING;
        totalRunTime = 0;
        worldResultAtEveryTick = new ArrayList<>();
        functions = new AssistFunctions();
        functions.setAllEntities(world.getAllEntities());
        functions.setEnvironmentVariables(world.getEnvironmentVariables());
        cancelReason = null;
    }
    public RunState getCurrentState(){
        return currentState;
    }
    public RunResultInfo getRunResultInfo() {
        ArrayList<WorldResultInfo> worldResultsInfo; //just for DONE state
        WorldResultInfo currentWorldResult; //just for RUNNING state
        //אם מצב הסימולציה בוצע או מושהה אז צריך את מצב הסימולציה בכל תיק, במושהה זה בשביל הבונוס
        if (currentState == RunState.DONE || currentState==RunState.PAUSED || currentState==RunState.CANCELLED) {
            worldResultsInfo = new ArrayList<>();
            for(World world : worldResultAtEveryTick){
                worldResultsInfo.add(world.getWorldResultInfo());
            }
            currentWorldResult=null;
        } else {//currentState = RUNNING
            currentWorldResult = world.getWorldResultInfo();
            worldResultsInfo = null;

        }
        return new RunResultInfo(ticks, startTime, totalRunTime, runID, currentState.toString(), currentWorldResult,worldResultsInfo, cancelReason);
    }
    public void setUserControl(String newStrState){
        RunState newEnumState = convertStrStateToEnumState(newStrState);
        //calculateTotalRunTime(newEnumState);
        currentState = newEnumState;
        if(newEnumState != RunState.PAUSED) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public void cancelRun(String cancelReason){
        this.cancelReason = cancelReason;
        currentState = RunState.CANCELLED;
    }

    RunState convertStrStateToEnumState(String strState){
        switch (strState){
            case "RESUME":
                return RunState.RUNNING;
            case "PAUSE":
                return RunState.PAUSED;
            case "STOP":
                return RunState.DONE;
        }
        return null;
    }

    public void runSimulation(DataFromUser detailsToRun) throws SimulationRunningException, IOException, ClassNotFoundException, InterruptedException {

        actionsBeforeSimulation(detailsToRun);
        while (simulationShouldRun()) {
            saveWorldResultAtCurrentTick();
            functions.setNumOfTicksInSimulation(ticks); // update in functions on which tick we are
            world.moveAllEntitiesOnGrid(); // 1. move all entities on grid
            runRulesOnEachEntity(makeListOfAllActionsThatShouldRun()); // 2. make a list of all the actions that should run
            ticks++;
        }
        actionsAfterSimulation();


    }
    void saveWorldResultAtCurrentTick() throws IOException, ClassNotFoundException {
        World newWorldResult = world.deepCopy();
        worldResultAtEveryTick.add(newWorldResult);
    }

    private void actionsBeforeSimulation(DataFromUser detailsToRun){
        updateDataFromUser(detailsToRun);
        ticks=1;
        world.generateAllEntitiesMapByDefinition(); // new
        world.generateDefinitionForSecondaryEntity(); // new
        world.generateRandomPositionsOnGrid(); // scatter the entities on the grid randomly
        startTime = System.currentTimeMillis();
        currentState=RunState.RUNNING;


    }
    private void actionsAfterSimulation(){
        currentState = RunState.DONE;


    }

    public void runRulesOnEachEntity(List<Action> actionsThatShouldRun) throws SimulationRunningException {
        ArrayList<Entity> entitiesToKill = new ArrayList<>();
        ArrayList<Entity> entitiesToCreate = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Entity>> entry : world.getAllEntities().entrySet()) {
            ArrayList<Entity> entityList = entry.getValue();
            for(Entity entity : entityList) {
                for(Action action : actionsThatShouldRun) {
                    if (actionShouldRunOnEntity(action, entity)) { // 4. check if the action works on the current instance
                        runAction(action, entity, entitiesToKill, entitiesToCreate);
                    }
                }  // 4.b if not -> skip to the next action
            }
        }
        world.killEntities(entitiesToKill);
        world.createEntities(entitiesToCreate);
    }
    public void runAction(Action action, Entity entity, ArrayList<Entity> entitiesToKill, ArrayList<Entity> entitiesToCreate) throws SimulationRunningException {
        if (actionHasSecondaryEntity(action)) { // 4.a if yes -> check if there is a secondary entity regarding the action
            // 5.a if yes:
            List<Entity> secondaryEntities = makeListOfSecondaryEntities(action); // make a list of all the secondary entities needs to activate
            if(secondaryEntities.isEmpty()){ // if no second entity has answered the condition
                Action newAction = createNewActionWithoutSecondaryEntity(action, entity);
                activateAction(newAction, entity, null, entitiesToKill, entitiesToCreate);
            }
            else {
                for (Entity secondary : secondaryEntities) { // go over all the secondary entities instances and make the action on main entity and secondary entity
                    activateAction(action, entity, secondary, entitiesToKill, entitiesToCreate); // need to send main and secondary entities to activate
                }
            }
        }
        else { // 5.b if not -> do the action on the current entity instance
            activateAction(action, entity, null, entitiesToKill, entitiesToCreate);
        }
    }
    private List<Entity> makeListOfSecondaryEntities(Action action) throws SimulationRunningException {
        SecondaryEntity secondaryEntityInfo = action.getSecondEntityInfo();
        Integer count = secondaryEntityInfo.getNumOfSecondEntities();
        List<Entity> result = new ArrayList<>();

        if(count == null || count > secondaryEntityInfo.getDefinition().getNumOfInstances()){
            // if count==ALL or that is bigger than the population -> count=population.
            count = secondaryEntityInfo.getDefinition().getNumOfInstances();
        }

        ArrayList<Entity> entityList = world.getAllEntities().get(secondaryEntityInfo.getName());
        ArrayList<Entity> shuffledSecondEntities = new ArrayList<>(entityList); // get a copy of secondary list
        Collections.shuffle(shuffledSecondEntities); // shuffle the list to make it random
        Condition condition = action.getSecondEntityInfo().getSelection(); // get the condition for selection

        int i=0; // if no one been chosen -> the list will be empty
        while(result.size() < count && i < shuffledSecondEntities.size()){
            // while we are under the count, or there are still entities in the array
            Entity secondEntityToAdd = shuffledSecondEntities.get(i++); // take the next random entity

            boolean selectionTrue=true;
            if(secondaryEntityInfo.getSelection() != null){ // if it has a selection condition
                setValueOfExpressionOnAction(condition, secondEntityToAdd, null);
                Property actionProp = secondEntityToAdd.getPropertyByName(action.getPropToChangeName());
                ParametersForAction params = new ParametersForAction(actionProp, secondEntityToAdd, null, ticks);
                if(condition instanceof SingleCondition)
                    selectionTrue = ((SingleCondition)condition).checkCondition(params);
                else
                    selectionTrue = ((MultipleCondition)condition).checkCondition(params);
            }

            if(selectionTrue){
                result.add(secondEntityToAdd);
            }
        }

        return result;
    }

    public Action createNewActionWithoutSecondaryEntity(Action action, Entity entity){
        // if the second entity list is empty -> ignore the secondary entity -> create a new list of conditions without it
        Action newAction=null;
        if(action instanceof MultipleCondition){
            ArrayList<Condition> conditions = ((MultipleCondition) action).getConditions();
            ArrayList<Condition> newConditions = new ArrayList<>();
            for(Condition c : conditions){ // go over the list of conditions and only perform the ones without the second entity
                if(!c.getMainEntityName().equals(action.getSecondEntityInfo().getName())){
                    newConditions.add(c);
                }
            }
            ArrayList<Action> thenActions = ((MultipleCondition) action).getThenActions();
            ArrayList<Action> newThen = new ArrayList<>();

            for(Action t : thenActions){
                if(!(t.getMainEntityName().equals(action.getSecondEntityInfo().getName()))){
                    newThen.add(t);
                }
            }
            if(newThen.isEmpty()){

            }

            ArrayList<Action> elseActions = ((MultipleCondition) action).getElseActions();
            ArrayList<Action> newElse = new ArrayList<>();
            for(Action e : elseActions){
                if(!(e.getMainEntityName().equals(action.getSecondEntityInfo().getName()))){
                    newElse.add(e);
                }
            }

            newAction = new MultipleCondition(entity.getName(), null, action.getPropToChangeName(),
                    newThen, newElse, ((MultipleCondition)action).getLogicSign(), newConditions);
        }

        return newAction;
    }
    public void activateAction(Action action, Entity entity, Entity secondary, ArrayList<Entity> entitiesToKill, ArrayList<Entity> entitiesToCreate) throws SimulationRunningException {
        ParametersForAction params = setParametersAndExpressionToAction(action, entity, secondary);

        if (action.activate(params)) { // need to kill
            entitiesToKill.add(entity);

            // checking if there are any entities to add to the "create entities" list
            if(action instanceof Replace){
                entitiesToCreate.add(((Replace)action).getEntityToCreate());
            } else if(action instanceof Proximity || action instanceof Condition){
                Action replaceAction = actionHasReplaceAction(action);
                if(replaceAction != null)
                    entitiesToCreate.add(((Replace)replaceAction).getEntityToCreate());
            }
        }
    }

    public Action actionHasReplaceAction(Action action){ // checks if one of the sub actions has Replace action in it.
        if(action instanceof Proximity){
            return searchSubActionsForReplaceAction(((Proximity)action).getThenActions());
        }
        else if (action instanceof Condition){
            // check in "then actions":
            Action replaceAction = searchSubActionsForReplaceAction(((Condition)action).getThenActions());
            if(replaceAction == null){
                // if not in "then actions", search in "else actions":
                return searchSubActionsForReplaceAction(((Condition)action).getElseActions());
            } else {
                return replaceAction;
            }
        } else {
            return null;
        }
    }

    public Action searchSubActionsForReplaceAction(List<Action> subActions){
        for(Action e : subActions){
            if(e instanceof Replace)
                return e;
            else if(e instanceof Condition || e instanceof Proximity){
                return actionHasReplaceAction(e);
            }
        }
        return null;
    }

    public ParametersForAction setParametersAndExpressionToAction(Action action, Entity entity, Entity secondary){
        ParametersForAction params = getParametersForAction(action, entity, secondary);

        setValueOfExpressionOnAction(action, entity, secondary);

        return params;
    }

    private void setValueOfExpressionOnAction(Action action, Entity entity, Entity secondary){
        if(action instanceof Decrease || action instanceof Increase || action instanceof Set || action instanceof Calculation){
            Object valExpression = getValueOfExpressionNEW(action, action.getExpressionNew(), entity, secondary);
            action.getExpressionNew().setValue(valExpression);

            if(action instanceof Calculation) {
                Object arg2 = getValueOfExpressionNEW(action, ((Calculation) action).getExpression2(), entity, secondary);
                ((Calculation) action).getExpression2().setValue(arg2);
            }
        } else if(action instanceof Proximity) { // there is no property
            Object valExpression = getValueOfExpressionNEW(action, action.getExpressionNew(), entity, secondary);
            action.getExpressionNew().setValue(valExpression);

            if(((Proximity) action).getThenActions() != null){
                for(Action thenAction : ((Proximity) action).getThenActions()){
                    setValueOfExpressionOnAction(thenAction, entity, secondary);
                }
            }
        } else if(action instanceof Kill || action instanceof Replace) {
            return;
        }

        if(action instanceof Condition){
            if(((Condition) action).getThenActions() != null)
                for (Action thenAction : ((Condition) action).getThenActions())
                    setValueOfExpressionOnAction(thenAction, entity, secondary);

            if(((Condition) action).getElseActions() != null)
                for (Action elseAction : ((Condition) action).getElseActions())
                    setValueOfExpressionOnAction(elseAction, entity, secondary);

            if(action instanceof SingleCondition){
                Expression valueExpression = action.getExpressionNew();
                Object val = getValueOfExpressionNEW(action, valueExpression, entity, secondary);
                action.getExpressionNew().setValue(val);

                Expression propertyExpression = ((SingleCondition)action).getPropertyExpression();
                Object prop = getValueOfExpressionNEW(action, propertyExpression, entity, secondary);
                ((SingleCondition)action).getPropertyExpression().setValue(prop);
            }
            if(action instanceof MultipleCondition){
                for(Condition condition : ((MultipleCondition)action).getConditions()){
                    setValueOfExpressionOnAction(condition, entity, secondary);
                }
            }
        }
    }

    private Object getValueOfExpressionNEW(Action action, Expression expression, Entity mainEntity, Entity secondary){
        // third entity is for Proximity (target entity) and Replace (the entity to create)

        if(expression.isNameOfFunction()){
            if(expression.getName().startsWith("environment")){
                return functions.environment(expression.getStringInParenthesis());
            } else if(expression.getName().startsWith("random")){
                return functions.random(Integer.parseInt(expression.getStringInParenthesis()));
            } else if(expression.getName().startsWith("evaluate")){
                if(action instanceof Proximity){
                    return functions.evaluate(expression.getStringInParenthesis(), mainEntity, secondary, null);
                } else if(action instanceof Replace){
                    Entity third = ((Replace)action).getEntityToCreate();
                    return functions.evaluate(expression.getStringInParenthesis(), mainEntity, secondary, third);
                }else {
                    return functions.evaluate(expression.getStringInParenthesis(), mainEntity, secondary, null);
                }
            } else if(expression.getName().startsWith("percent")){
                if(action instanceof Proximity){
                    return functions.evaluate(expression.getStringInParenthesis(), mainEntity, secondary, null);
                } else if(action instanceof Replace){
                    Entity third = ((Replace)action).getEntityToCreate();
                    return functions.evaluate(expression.getStringInParenthesis(), mainEntity, secondary, third);
                } else {
                    return functions.percent(expression.getStringInParenthesis(), mainEntity, secondary, null);
                }
            } else { // ticks
                return functions.ticks(expression.getStringInParenthesis(), mainEntity);
            }
        } else if(expression.isNameOfProperty(mainEntity)){
            return mainEntity.getProperties().get(expression.getName()).getVal();
        } else {
            if(expression.isANumber())
                return Double.parseDouble(expression.getName());
            else if(expression.isBoolean())
                return Boolean.parseBoolean(expression.getName());
            else
                return expression.getName();
        }
    }
    ParametersForAction getParametersForAction(Action action, Entity mainEntity, Entity secondaryEntity){
        ParametersForAction params;

        if(action instanceof Proximity){
            ((Proximity) action).setSourcePos(mainEntity.getPosition());

            ArrayList<Action> thenActions = ((Proximity)action).getThenActions();
            ArrayList<ParametersForAction> thenParams = getParametersForCondition(thenActions, mainEntity, secondaryEntity);
            params = new ParametersForCondition(null, mainEntity, secondaryEntity, ticks, thenParams, null);


        } else if(action instanceof Replace){
            String nameToCreate = ((Replace)action).getEntityToCreateName();
            Entity toCreate = new Entity(nameToCreate, null); // fill properties in Replace
            ((Replace)action).setEntityToCreate(toCreate);
            // in main entity send an entity to KILL
            params = new ParametersForAction(null, mainEntity, null, ticks);
        }
        else if(!(action instanceof Condition)) {
            Property mainProp = mainEntity.getPropertyByName(action.getPropToChangeName());
            params = new ParametersForAction(mainProp, mainEntity, secondaryEntity, ticks);
        }
        else { // add parameters for conditions:
            // add parameters for then actions:
            ArrayList<Action> thenActions = ((Condition)action).getThenActions();
            ArrayList<ParametersForAction> thenParams = getParametersForCondition(thenActions, mainEntity, secondaryEntity);

            // add parameters for else actions:
            ArrayList<Action> elseActions = ((Condition)action).getElseActions();
            ArrayList<ParametersForAction> elseParams = getParametersForCondition(elseActions, mainEntity, secondaryEntity);

            if(action instanceof SingleCondition) {
                Property mainProp = mainEntity.getPropertyByName(action.getPropToChangeName());
                params = new ParametersForCondition(mainProp, mainEntity, secondaryEntity, ticks, thenParams, elseParams);
            }
            else { // multiple condition
                params = getParamsForMulCon((MultipleCondition) action, mainEntity, secondaryEntity, thenParams, elseParams);
            }
        }
        return params;
    }

    ArrayList<ParametersForAction> getParametersForCondition(ArrayList<Action> actionList, Entity mainEntity, Entity secondEntity){
        ArrayList<ParametersForAction> params = null;

        if(actionList != null){
            params = new ArrayList<>();
            for(Action a : actionList){
                params.add(getParametersForAction(a, mainEntity, secondEntity));
            }
        }
        return params;
    }

    private ParametersForMultipleCondition getParamsForMulCon(MultipleCondition action, Entity mainEntity, Entity secondEntity,
                                                              ArrayList<ParametersForAction> thenParams, ArrayList<ParametersForAction> elseParams){
        ArrayList<ParametersForAction> conditionParams = new ArrayList<>();
        ArrayList<Condition> conditions = action.getConditions();

        for(Condition c : conditions){
            if(c instanceof SingleCondition){
                Property cProp =  mainEntity.getPropertyByName(c.getPropToChangeName());
                conditionParams.add(new ParametersForAction(cProp, mainEntity, secondEntity, ticks));
            } else { // multiple condition
                conditionParams.add(getParamsForMulCon((MultipleCondition) c, mainEntity, secondEntity, null, null));
            }
        }
        return new ParametersForMultipleCondition(null, mainEntity, secondEntity, ticks, thenParams, elseParams, conditionParams);
    }

    private boolean actionHasSecondaryEntity(Action action){
        return action.getSecondEntityInfo() != null;
    }
    private List<Rule> makeListOfAllRulesThatShouldRun(){
        List<Rule> rulesThatShouldRun = new ArrayList<>();
        for (Rule rule : world.getRules()) {
            if (ruleShouldRun(rule))
                rulesThatShouldRun.add(rule);
        }
        return rulesThatShouldRun;
    }
    private List<Action> makeListOfAllActionsThatShouldRun(){
        List<Rule> rulesThatShouldRun = makeListOfAllRulesThatShouldRun();
        List<Action> actionsThatShouldRun = new ArrayList<>();
        for (Rule rule : rulesThatShouldRun) {
            actionsThatShouldRun.addAll(rule.getActions());
        }
        return actionsThatShouldRun;
    }

    private void updateDataFromUser(DataFromUser detailsToRun){
        runID = detailsToRun.getRunID();
        detailsToRun.getPopulation().forEach((entityName, amount)->world.setEntitiesPopulation(entityName, amount));
        detailsToRun.getEnvironment().forEach((varName, value)-> {
            try {
                world.setEnvironmentValue(varName, value);
            } catch (SimulationRunningException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private Boolean ruleShouldRun(Rule rule){
        return ticks%rule.getTicks()==0 && Math.random()<=rule.getProbability();
    }
    private Boolean actionShouldRunOnEntity(Action action, Entity entity){
        return action.getMainEntityName().equals(entity.getName());
    }

    private Boolean simulationShouldRun() {
        Integer numOfSecondsToEnd = world.getEndConditionValueByType("seconds");
        Integer numOfTicksToEnd = world.getEndConditionValueByType("ticks");

        if(numOfSecondsToEnd != null && System.currentTimeMillis() - startTime > numOfSecondsToEnd*1000L)
            return false;
        else if (numOfTicksToEnd != null && numOfTicksToEnd < ticks)
            return false;
        else if (currentState == RunState.DONE)
            return false;
        else if(currentState == RunState.CANCELLED)
            return false;
        synchronized (this) {
            while (currentState == RunState.PAUSED) {
                try {
                    this.wait();
                }catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                if(currentState==RunState.DONE)
                    return false;

            }
        }

        return true;
    }

}
