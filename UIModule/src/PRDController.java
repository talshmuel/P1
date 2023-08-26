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
    private TableView<NameValueEntry> environmentTable;
    @FXML
    private TableColumn<NameValueEntry, String> nameColumn;
    @FXML
    private TableColumn<NameValueEntry, String> typeColumn;
    @FXML
    private TableColumn<NameValueEntry, Object> bottomColumn;
    @FXML
    private TableColumn<NameValueEntry, Object> topColumn;
    @FXML
    private TableColumn<NameValueEntry, String> valueColumn;
    @FXML
    private TableView<EntityValueEntry> entityTable;
    @FXML
    private TableColumn<EntityValueEntry, String> entityNameColumn;
    @FXML
    private TableColumn<EntityValueEntry, String> quantityColumn;

    private void setEntityTable(){
        entityNameColumn.setCellValueFactory(new PropertyValueFactory<EntityValueEntry, String>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<EntityValueEntry, String>("quantity"));

        entityTable.setEditable(true);
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        ArrayList<EntityInfo> entityInfo = simulationInfo.getEntities();
        for(EntityInfo e : entityInfo){
            entityTable.getItems().add(new EntityValueEntry(e.getName(), ""));
        }

        quantityColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            EntityValueEntry item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setPopulation(newValue);
        });
    }

    @FXML
    void handleEntityEditCommit(TableColumn.CellEditEvent<EntityValueEntry, String> event) {
        EntityValueEntry item = event.getTableView().getItems().get(event.getTablePosition().getRow());
        String newValue = event.getNewValue();
        item.setPopulation(newValue);
        entityTable.refresh();
    }

    private void setEnvironmentTable(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<NameValueEntry, String>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<NameValueEntry, String>("type"));
        bottomColumn.setCellValueFactory(new PropertyValueFactory<NameValueEntry, Object>("bottom"));
        topColumn.setCellValueFactory(new PropertyValueFactory<NameValueEntry, Object>("top"));

        environmentTable.setEditable(true);
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        for(PropertyInfo p : definitions) {
            environmentTable.getItems().add(new NameValueEntry(p.getName(), p.getType(), p.getBottomLimit(), p.getTopLimit(), ""));
        }

        valueColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            NameValueEntry item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setValue(newValue);
        });
    }

    @FXML
    private void handleEditCommit(TableColumn.CellEditEvent<NameValueEntry, String> event) {
        NameValueEntry item = event.getTableView().getItems().get(event.getTablePosition().getRow());
        String newValue = event.getNewValue();
        item.setValue(newValue);
        environmentTable.refresh();
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
