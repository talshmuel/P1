package engine;

import data.transfer.object.DataFromUser;
import data.transfer.object.definition.*;
import data.transfer.object.run.result.RunResultInfo;
import run.manager.RunManager;
import run.manager.RunManagerInterface;
import world.creator.WorldCreatorXML;
import world.creator.XMLFileException;
import world.entity.EntityDefinition;
import world.World;
import world.property.api.*;
import world.property.impl.*;
import world.rule.Rule;
import world.rule.action.*;
import xml.reader.XMLReader;
import xml.reader.schema.generated.v2.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Engine implements EngineInterface, Serializable {
    RunManagerInterface runManager;
    World worldDefinition;
    ExecutorService simulationsExecutor;


    public Engine(){
        runManager = new RunManager();
    }


    @Override
    public void createSimulationByXMLFile(String fileName) throws XMLFileException {
        XMLReader reader = new XMLReader();
        PRDWorld prdWorld = reader.validateXMLFileAndCreatePRDWorld(fileName);

        if(prdWorld != null){ // if file opened successfully, but the data is incorrect, then prdWorld would be null
            WorldCreatorXML worldCreator = new WorldCreatorXML();
            worldDefinition = worldCreator.createWorldFromXMLFile(prdWorld);
            runManager.setWorldDefinition(worldDefinition);
            simulationsExecutor = Executors.newFixedThreadPool(worldDefinition.getNumOfThreads());
        }
    }
    @Override
    public ArrayList<PropertyInfo> getEnvironmentDefinitions(){
        ArrayList<PropertyInfo> res = new ArrayList<>();
        for(PropertyDefinition propertyDef : worldDefinition.getEnvironmentDefinition()){
            PropertyInfo item = new PropertyInfo(propertyDef.getName(), propertyDef.getType(),
                    propertyDef.getTopLimit(), propertyDef.getBottomLimit(), false);
            res.add(item);
        }
        return res;
    }
    @Override
    public ArrayList<PropertyValueInfo> getEnvironmentValues() {
        return worldDefinition.getEnvironmentValues();
    }





    @Override
    public void runSimulation(DataFromUser detailsToRun) {
        simulationsExecutor.execute(() -> {
            runManager.runSimulation(detailsToRun);
        });
    }

    @Override
    public void setUserControlOnSpecificRun(int runID, String userControl) {
        runManager.setUserControlOnSpecificRun(runID, userControl);
    }

    @Override
    public String getCurrentStateOfSpecificRun(int runID) {
        return runManager.getCurrentStateOfSpecificRun(runID);
    }


    @Override
    public RunResultInfo displayRunResultsInformation(int runID) {
        return runManager.getSpecificRunResult(runID);
    }


    @Override
    public SimulationInfo displaySimulationDefinitionInformation(){
        return new SimulationInfo(displayEntitiesInformation(), displayRulesInformation(), displayTerminationInfo(), displayEnvironmentVariablesInfo(),
                worldDefinition.getNumOfThreads(), (worldDefinition.getGrid().getNumOfRows()*worldDefinition.getGrid().getNumOfCols()));
    }

    @Override
    public void cleanup() {
        if (worldDefinition != null)
            worldDefinition.cleanup();

        runManager.cleanup();
    }

    private Map<String, PropertyInfo> displayEnvironmentVariablesInfo(){
        Map<String, PropertyInfo> environmentInfo = new HashMap<>();
        Map<String, Property> environmentVariables = worldDefinition.getEnvironmentVariables();

        for(String name : environmentVariables.keySet()){
            PropertyDefinition propDef = environmentVariables.get(name).getDefinition();
            Object topLimit = propDef.getTopLimit();
            Object bottomLimit = propDef.getBottomLimit();
            String type;

            if(propDef instanceof IntegerPropertyDefinition){
                type = "Integer";
            } else if (propDef instanceof FloatPropertyDefinition){
                type = "Float";
            } else if (propDef instanceof BooleanPropertyDefinition){
                type = "Boolean";
            } else{ //  if (propDef instanceof StringPropertyDefinition){
                type = "String";
            }

            PropertyInfo propInfo = new PropertyInfo(name, type, topLimit, bottomLimit, false);
            environmentInfo.put(name, propInfo);
        }
        return environmentInfo;
    }
    private ArrayList<EntityInfo> displayEntitiesInformation() {
        ArrayList<EntityInfo> entitiesInfoArray = new ArrayList<>();
        ArrayList<EntityDefinition> entityDef = worldDefinition.getEntitiesDefinition();

        for (EntityDefinition e : entityDef){
            ArrayList<PropertyInfo> propertiesInfo = displayPropertiesInfo(e);
            EntityInfo entityInfoItem = new EntityInfo(e.getName(), e.getNumOfInstances(), propertiesInfo);
            entitiesInfoArray.add(entityInfoItem);
        }
        return entitiesInfoArray;
    }
    private ArrayList<PropertyInfo> displayPropertiesInfo(EntityDefinition e) {
        ArrayList<PropertyInfo> propertiesInfo = new ArrayList<>();
        e.getPropsDef().values().forEach((propDef)->{
            PropertyInfo propInfoItem = new PropertyInfo(propDef.getName(),propDef.getType(),propDef.getTopLimit(),propDef.getBottomLimit(),propDef.getRandomlyInitialized());
            propertiesInfo.add(propInfoItem);
        });
        return propertiesInfo;
    }
    private ArrayList<RuleInfo> displayRulesInformation(){
        ArrayList<RuleInfo> rulesInfoArray = new ArrayList<>();
        ArrayList<Rule> rulesArray = worldDefinition.getRules();

        for(Rule r : rulesArray) {
            ArrayList<ActionInfo> actionsInfoArray = displayActionsInformation(r);
            RuleInfo rulesInfoItem = new RuleInfo(r.getName(), r.getTicks(), r.getProbability(), r.getNumOfActions(), actionsInfoArray);
            rulesInfoArray.add(rulesInfoItem);
        }
        return rulesInfoArray;
    }

    private ArrayList<ActionInfo> displayActionsInformation(Rule r){
        ArrayList<ActionInfo> res = new ArrayList<>();
        ArrayList<Action> actionsArray = r.getActions();
        for(Action a : actionsArray) {
            res.add(a.getActionInfo());
        }
        return res;
    }
    private ArrayList<TerminationInfo> displayTerminationInfo(){

        ArrayList<TerminationInfo> terminationInfosArray = new ArrayList<>();
        Integer ticksVal = worldDefinition.getEndConditionValueByType("ticks");
        Integer secondsVal = worldDefinition.getEndConditionValueByType("seconds");
        if(ticksVal!=null) {
            TerminationInfo item1 = new TerminationInfo("ticks", ticksVal);
            terminationInfosArray.add(item1);

        }
        if(secondsVal!=null) {
            TerminationInfo item2 = new TerminationInfo("seconds", secondsVal);
            terminationInfosArray.add(item2);

        }
        if(secondsVal==null && ticksVal==null){
            TerminationInfo item3 = new TerminationInfo("user", null);
            terminationInfosArray.add(item3);
        }
        return terminationInfosArray;
    }




}










