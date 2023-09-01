package UI.page.details;

import UI.PRDController;
import data.transfer.object.definition.*;
import engine.EngineInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.util.ArrayList;

public class DetailsPageController {
    private EngineInterface engine;
    private PRDController mainController;

    @FXML
    private TreeView<String> worldDetailsTree;
    @FXML
    private ListView<String> entityPropertiesListView;


    @FXML
    void showWorldTree(ActionEvent event) {


    }


    public void setWorldDetailsTree(){
        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        TreeItem<String> worldItem = new TreeItem<>("World");

        TreeItem <String> entitiesItem = new TreeItem<>("Entities");
        setChildrenOfEntitiesItem(simulationInfo.getEntities(), entitiesItem);

        TreeItem <String> environmentItem = new TreeItem<>("Environment variables");
        //setChildrenOfEnvironmentItem(simulationInfo. , environmentItem);

        TreeItem <String> rulesItem = new TreeItem<>("Rules");
        //setChildrenOfRulesItem(simulationInfo.getRules(), rulesItem);

        TreeItem <String> endConditionsItem = new TreeItem<>("End conditions");
        //setChildrenOfEndConditionsItem(simulationInfo.getEndConditions(), endConditionsItem);


        worldItem.getChildren().addAll(entitiesItem, environmentItem, rulesItem, endConditionsItem);
        worldDetailsTree.setRoot(worldItem);
    }
    private void setChildrenOfEntitiesItem(ArrayList<EntityInfo> entitiesInfo, TreeItem <String> entitiesItem){
        for(EntityInfo entityInfo:entitiesInfo){
            TreeItem<String> entityItem = new TreeItem<>(entityInfo.getName());
            entitiesItem.getChildren().add(entityItem);
        }
    }
    //    private void setChildrenOfEnvironmentItem(ArrayList<TerminationInfo> environmentInfo, TreeItem <String> environmentItem){
//
//    }
    private void setChildrenOfRulesItem(ArrayList<RuleInfo> rulesInfo, TreeItem <String> rulesItem){

    }
    private void setChildrenOfEndConditionsItem(ArrayList<TerminationInfo> terminationInfo, TreeItem <String> entitiesItem){

    }

    @FXML
    public void handleSelectWorldTreeItem() {
        TreeItem<String> selected = worldDetailsTree.getSelectionModel().getSelectedItem();
        if (selected!= null) {
            SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
            if(selected.getParent()!=null) {
                entityPropertiesListView.getItems().clear();
                switch (selected.getParent().getValue()) {
                    case "Entities":
                        showPropertiesOfEntity(simulationInfo, selected.getValue());
                        break;
                    case "Environment variables":
                        break;
                    case "Rules":
                        break;
                    case "End conditions":
                        break;

                }
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
