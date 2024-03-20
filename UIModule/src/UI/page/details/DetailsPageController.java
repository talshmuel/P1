package UI.page.details;

import UI.PRDController;
import data.transfer.object.definition.*;
import engine.EngineInterface;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;


import java.util.ArrayList;

public class DetailsPageController {
    private EngineInterface engine;
    private PRDController mainController;
    @FXML private TreeView<String> worldDetailsTree;
    @FXML private ListView<String> entityPropertiesListView;
    @FXML private ScrollBar sideScroller;
    @FXML private ScrollBar rightScroller;
    @FXML private SplitPane mainSplitPane;



    @FXML
    public void initialize() {
        sideScroller.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainSplitPane.setTranslateY(-newValue.doubleValue());
        });

        rightScroller.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainSplitPane.setTranslateX(-newValue.doubleValue());
        });
    }



    @FXML public void handleSideScroll(ScrollEvent scrollEvent) {
        double deltaY = scrollEvent.getDeltaY();
        sideScroller.setValue(sideScroller.getValue() + deltaY);
    }

    public void handleRightScroller(ScrollEvent scrollEvent) {
        double deltaY = scrollEvent.getDeltaY();
        rightScroller.setValue(rightScroller.getValue() + deltaY);
    }


    public void setWorldDetailsTree() {
        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        TreeItem<String> worldItem = new TreeItem<>("World");

        TreeItem<String> entitiesItem = new TreeItem<>("Entities");
        setChildrenOfEntitiesItem(simulationInfo.getEntities(), entitiesItem);

        TreeItem<String> environmentItem = new TreeItem<>("Environment variables");


        TreeItem<String> rulesItem = new TreeItem<>("Rules");
        setChildrenOfAllRulesItem(simulationInfo.getRules(), rulesItem);

        rulesItem.getChildren().forEach((ruleItem) -> {
            setChildrenOfRuleItem(findRule(simulationInfo.getRules(), ruleItem.getValue()) ,ruleItem);
        });

        TreeItem<String> endConditionsItem = new TreeItem<>("End conditions");


        worldItem.getChildren().addAll(entitiesItem, environmentItem, rulesItem, endConditionsItem);
        worldDetailsTree.setRoot(worldItem);
    }
    private void setChildrenOfEntitiesItem(ArrayList<EntityInfo> entitiesInfo, TreeItem <String> entitiesItem){
        for(EntityInfo entityInfo:entitiesInfo){
            TreeItem<String> entityItem = new TreeItem<>(entityInfo.getName());
            entitiesItem.getChildren().add(entityItem);
        }
    }
    RuleInfo findRule(ArrayList<RuleInfo> rules, String ruleNameToFind){
        for(RuleInfo ruleInfo : rules){
            if(ruleInfo.getName()==ruleNameToFind)
                return ruleInfo;
        }
        return null;
    }

    private void setChildrenOfAllRulesItem(ArrayList<RuleInfo> rulesInfo, TreeItem <String> rulesItem){
        for(RuleInfo ruleInfo:rulesInfo){
            TreeItem<String> ruleItem = new TreeItem<>(ruleInfo.getName());
            rulesItem.getChildren().add(ruleItem);
        }
    }

    private void setChildrenOfRuleItem(RuleInfo ruleInfo, TreeItem <String> ruleItem){
        int i=1;
        for(ActionInfo actionInfo : ruleInfo.getActions()){
            TreeItem<String> actionItem = new TreeItem<>(String.valueOf(i++));
            ruleItem.getChildren().add(actionItem);
        }
    }




    @FXML
    public void handleSelectWorldTreeItem() {
        TreeItem<String> selected = worldDetailsTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
            if (selected.getParent() != null) {
                entityPropertiesListView.getItems().clear();
                switch (selected.getParent().getValue()) {
                    case "Entities":
                        showPropertiesOfEntity(simulationInfo, selected.getValue());
                        break;
                    case "Rules":
                        showActivationOfRule(simulationInfo, selected.getValue());
                        break;
                    default:
                        if (selected.getParent().getParent() != null && selected.getParent().getParent().getValue() == "Rules")
                            showActionsDefinition(simulationInfo, Integer.parseInt(selected.getValue()), selected.getParent().getValue());
                        break;
                }


                switch (selected.getValue()) {
                    case "Environment variables":
                        showEnvironment(simulationInfo);
                        break;
                    case "End conditions":
                        showEndConditions(simulationInfo);
                        break;


                }
            }
        }

    }

    private void showActionsDefinition(SimulationInfo simulationInfo, int actionNumber, String ruleName){
        RuleInfo rule = findRule(simulationInfo.getRules(), ruleName);

        entityPropertiesListView.getItems().add(rule.getActions().get(actionNumber-1).toString());


    }

    private void showEndConditions(SimulationInfo simulationInfo){
        simulationInfo.getEndConditions().forEach((endCond)->{
            entityPropertiesListView.getItems().add(endCond.toString());

        });
    }
    private void showEnvironment(SimulationInfo simulationInfo){
        simulationInfo.getEnvironmentVariables().forEach((envName, envProp)->{
            entityPropertiesListView.getItems().add(getStringProperty(envProp));
        });


    }
    private void showActivationOfRule(SimulationInfo simulationInfo, String ruleName){
        for(RuleInfo ruleInfo : simulationInfo.getRules()){
            if(ruleInfo.getName().equals(ruleName)) {
                entityPropertiesListView.getItems().add(ruleInfo.toString());
            }
        }
    }

    private void showPropertiesOfEntity(SimulationInfo simulationInfo, String entityName){
        for(EntityInfo entityInfo : simulationInfo.getEntities()){
            if(entityInfo.getName().equals(entityName)){
                for(PropertyInfo prop : entityInfo.getProperties()){
                    entityPropertiesListView.getItems().add(getStringProperty(prop));
                }

            }
        }
    }

    String getStringProperty(PropertyInfo prop){
        String res;
        res = "Property name: "+ prop.getName()+"\nType: "+ prop.getType();
        if(prop.getTopLimit()!= null)
            res = res+"\nRange: "+ prop.getBottomLimit()+" - "+prop.getTopLimit();
        if(prop.getIsRandomInit())
            res = res+"\nProperty is randomly initialize";


        return res;
    }

    public void setMainController(PRDController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }



}
