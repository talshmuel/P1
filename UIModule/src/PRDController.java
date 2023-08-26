import data.transfer.object.definition.*;
import engine.Engine;
import engine.EngineInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

    ///////////////////////////// gon /////////////////////////////
    @FXML
    private TableView<EnvironmentInfo> environmentTable;
    @FXML
    private TableColumn<EnvironmentInfo, String> nameColumn;
    @FXML
    private TableColumn<EnvironmentInfo, String> typeColumn;
    @FXML
    private TableColumn<EnvironmentInfo, Object> bottomColumn;
    @FXML
    private TableColumn<EnvironmentInfo, Object> topColumn;
    @FXML
    private TableColumn<EnvironmentInfo, String> valueColumn;

    @FXML
    private TableView<EntityInfo> entityTable;
    @FXML
    private TableColumn<EntityInfo, String> entityNameColumn;
    @FXML
    private TableColumn<EntityInfo, String> quantityColumn;

    private void setEntityTable(){
        entityNameColumn.setCellValueFactory(new PropertyValueFactory<EntityInfo, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<EntityInfo, String>("quantity"));
        entityTable.setEditable(true);
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        ArrayList<EntityInfo> entityInfo = simulationInfo.getEntities();
        for(EntityInfo e : entityInfo){
            entityTable.getItems().add(new EntityInfo(e.getName(), 0, null));
        }

        quantityColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue(); // todo: add exception
            // todo: add this value to world's entity population
            EntityInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setPopulation(Integer.parseInt(newValue));
        });
    }

//    @FXML
//    void handleEntityEditCommit(TableColumn.CellEditEvent<EntityInfo, String> event) {
//        String newValue = event.getNewValue();
//        EntityInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
//        item.setPopulation(Integer.parseInt(newValue));
//        entityTable.refresh();
//    }

    private void setEnvironmentTable(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        bottomColumn.setCellValueFactory(new PropertyValueFactory<>("bottomLimit"));
        topColumn.setCellValueFactory(new PropertyValueFactory<>("topLimit"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        environmentTable.setEditable(true);
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        // Populate data in the TableView (replace this with your data)
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        for(PropertyInfo p : definitions) {
            environmentTable.getItems().add(new EnvironmentInfo(p.getName(), p.getType(), (Integer)p.getBottomLimit(), (Integer)p.getTopLimit(), null));
        }

        valueColumn.setOnEditCommit(event -> {
            Object newValue = event.getNewValue(); // todo: add exceptions
            // todo: add this value to world
            EnvironmentInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setValue(newValue);
        });
    }

    @FXML
    private void handleEditCommit(TableColumn.CellEditEvent<EnvironmentInfo, String> event) {
        EnvironmentInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
        String newValue = event.getNewValue();
        item.setValue(newValue);
        environmentTable.refresh();
    }

    private void setEnvironmentVariableValueFromUser(){
        PropertyInfo propertyInfo;
    }

    ///////////////////////////// gon /////////////////////////////

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
            setEnvironmentTable(); // gon
            setEntityTable(); // gon
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
