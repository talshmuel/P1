import data.transfer.object.definition.*;
import engine.Engine;
import engine.EngineInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class PRDController {
    private EngineInterface engine;

    @FXML
    private TextField filePathField;

    @FXML
    private Button loadXMLButton;

    @FXML
    private Button queueManagementBotton;

    @FXML
    private TreeView<String> worldDetailsTree;

    @FXML
    void openFileChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            filePathField.appendText(selectedFile.getAbsolutePath());
            try {
                engine = new Engine();
                engine.createSimulationByXMLFile(selectedFile.getAbsolutePath());
            }catch (Exception e){
                //צריך לבדוק כאן איך מתמודדים עם שגיאות
            }
            setWorldDetailsTree();
        }
    }
    @FXML
    void showWorldTree(ActionEvent event) {


    }
//    @FXML
//    private void initialize(){
//        engine = new Engine();
//    }
    private void setWorldDetailsTree(){
        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        TreeItem<String> worldItem = new TreeItem<>("World");

        TreeItem <String> entitiesItem = new TreeItem<>("Entities");
        setChildrenOfEntitiesItem(simulationInfo.getEntities(), entitiesItem);

        TreeItem <String> environmentItem = new TreeItem<>("Environment variables");
//        setChildrenOfEnvironmentItem(simulationInfo. , environmentItem);

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
            TreeItem<String> propertiesItem = new TreeItem<>("Properties");
            for (PropertyInfo propInfo : entityInfo.getProperties()){
                TreeItem<String> propItem = new TreeItem<>(propInfo.getName());
                propertiesItem.getChildren().add(propItem);
            }
            entityItem.getChildren().add(propertiesItem);
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
    void handleSelectWorldTreeItem() {
        TreeItem<String> selected = worldDetailsTree.getSelectionModel().getSelectedItem();
        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
//        switch (selected.getValue()){
//            case "Entities":
//                break;
//            case "Environment variables":
//                break;
//            case "Rules":
//                break;
//            case "End conditions":
//                break;
//
//        }
    }

    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }
}
