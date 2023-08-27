import data.transfer.object.definition.*;
import engine.Engine;
import engine.EngineInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


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
    private TableView<EntityInfo> entityTable;
    @FXML
    private TableColumn<EntityInfo, String> entityNameCol;
    @FXML
    private TableColumn<EntityInfo, Integer> entityPopCol;
    @FXML
    private TableView<EnvironmentInfo> envTable;
    @FXML
    private TableColumn<EnvironmentInfo, String> envNameCol;
    @FXML
    private TableColumn<EnvironmentInfo, String> envTypeCol;
    @FXML
    private TableColumn<EnvironmentInfo, String> envValueCol;

    public void setEnvironmentTable() {
        envNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        envTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        envValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Populate the table with environment variables
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        ObservableList<EnvironmentInfo> environmentVariables = FXCollections.observableArrayList();
        for (PropertyInfo p : definitions) {
            environmentVariables.add(new EnvironmentInfo(p.getName(), p.getType(), p.getBottomLimit(), p.getTopLimit()));
        }
        envTable.setItems(environmentVariables);

        envValueCol.setCellFactory(column -> new TableCell<EnvironmentInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    EnvironmentInfo variable = (EnvironmentInfo) getTableRow().getItem(); // Get the EnvironmentInfo object
                    Node inputNode;

                    switch (variable.getType()) {
                        case "Integer": {
                            double top = (int) variable.getTopLimit();
                            double bottom = (int) variable.getBottomLimit();
                            Slider slider = new Slider(bottom, top, bottom);
                            slider.setPrefWidth(100);
                            Label currentValueLabel = new Label();
                            currentValueLabel.setStyle("-fx-text-fill: green;");
                            currentValueLabel.textProperty().bind(slider.valueProperty().asString("%.2f"));
                            Label bottomLimitLabel = new Label(variable.getBottomLimit().toString());
                            Label topLimitLabel = new Label(variable.getTopLimit().toString());
                            HBox hbox =  new HBox(10);
                            hbox.setAlignment(Pos.CENTER);
                            hbox.getChildren().addAll(currentValueLabel, bottomLimitLabel, slider, topLimitLabel);

                            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                                variable.setValue(newValue);
                            });
                            inputNode = hbox;
                            break;
                        }
                        case "Float": {
                            double top = (double) variable.getTopLimit();
                            double bottom = (double) variable.getBottomLimit();
                            Slider slider = new Slider(bottom, top, bottom);
                            slider.setPrefWidth(100);
                            Label currentValueLabel = new Label();
                            currentValueLabel.setStyle("-fx-text-fill: blue;");
                            currentValueLabel.textProperty().bind(slider.valueProperty().asString("%.2f"));
                            Label bottomLimitLabel = new Label(variable.getBottomLimit().toString());
                            Label topLimitLabel = new Label(variable.getTopLimit().toString());
                            HBox hbox =  new HBox(10);
                            hbox.setAlignment(Pos.CENTER);
                            hbox.getChildren().addAll(currentValueLabel, bottomLimitLabel, slider, topLimitLabel);

                            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                                variable.setValue(newValue);
                            });
                            inputNode = hbox;
                            break;
                        }
                        case "Boolean":
                            CheckBox trueCheckBox = new CheckBox("True");
                            CheckBox falseCheckBox = new CheckBox("False");
                            trueCheckBox.setSelected(false);
                            falseCheckBox.setSelected(false);

                            trueCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue) {
                                    falseCheckBox.setSelected(false);
                                    variable.setValue(Boolean.toString(true));
                                } else if (!falseCheckBox.isSelected()) {
                                    trueCheckBox.setSelected(true); // Enforce selecting one
                                }
                            });

                            falseCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue) {
                                    trueCheckBox.setSelected(false);
                                    variable.setValue(Boolean.toString(false));
                                } else if (!trueCheckBox.isSelected()) {
                                    falseCheckBox.setSelected(true); // Enforce selecting one
                                }
                            });
                            HBox hbox = new HBox(trueCheckBox, falseCheckBox);
                            hbox.setAlignment(Pos.CENTER);
                            inputNode = hbox;
                            break;
                        default:
                            TextField textField = new TextField();
                            textField.setPrefHeight(15);
                            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                                // Update the variable's value
                                variable.setValue(newValue);
                            });
                            inputNode = textField;
                            break;
                    }

                    setGraphic(inputNode);
                }
            }
        });
    }


    private void setEntityTable(){
        entityNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        entityPopCol.setCellValueFactory(new PropertyValueFactory<>("population"));

        // Populate the entity table with data
        SimulationInfo simulationInfo = engine.displaySimulationDefinitionInformation();
        ArrayList<EntityInfo> entities = simulationInfo.getEntities();
        for(EntityInfo e : entities){
            entityTable.getItems().add(new EntityInfo(e.getName(), 0, null));
        }
        ObservableList<EntityInfo> entityData = FXCollections.observableArrayList(entities);
        entityTable.setItems(entityData);

        entityPopCol.setCellFactory(column -> new TableCell<EntityInfo, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    EntityInfo entity = getTableView().getItems().get(getIndex());
                    TextField textField = new TextField("");
                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.matches("\\d*")) {
                            entity.setPopulation(Integer.parseInt(newValue));
                        } else {
                            textField.setText(oldValue);
                        }
                    });
                    setGraphic(textField);
                }
            }
        });

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
