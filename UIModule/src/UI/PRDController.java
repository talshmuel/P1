package UI;

import UI.page.details.DetailsPageController;
import data.transfer.object.EndSimulationData;
import data.transfer.object.definition.*;
import engine.Engine;
import engine.EngineInterface;
import exception.DivisionByZeroException;
import exception.IncompatibleAction;
import exception.IncompatibleType;
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
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import world.property.impl.Property;


public class PRDController {
    @FXML private SplitPane detailsPageComponent;
    @FXML private DetailsPageController detailsPageComponentController;

//    @FXML private SplitPane resultsPageComponent;//זה כנראה לא יהיה ספליט פיין צריך לעדכן!!!
//    @FXML private DetailsPageController resultsPageComponentController;

    private EngineInterface engine;

    @FXML
    private TextField filePathField;

    @FXML
    private Button loadXMLButton;

    @FXML
    private Button queueManagementBotton;


    ///////////////////////////// gon /////////////////////////////

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab detailsTab;
    @FXML
    private Tab resultsTab;
    @FXML
    private Tab newExecutionTab;
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
    @FXML
    private Button startSimulation;
    @FXML
    private Button clearSimulation;

    /** New Execution logic **/
    @FXML
    void handleStartSimulation(ActionEvent event) throws IncompatibleAction, DivisionByZeroException, IncompatibleType {
        // update environment variables. טל תתעלמי מזה בינתיים
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        ArrayList<PropertyValueInfo> values = engine.getEnvironmentValues();
        for(PropertyInfo d : definitions){
            System.out.println("name: " + d.getName());
            System.out.println("type: " + d.getType());
            System.out.println("bottom: " + d.getBottomLimit());
            System.out.println("top: " + d.getTopLimit());
        }
        for(PropertyValueInfo v : values){
            System.out.println("name: " + v.getName());
            System.out.println("value: " + v.getVal());
        }

        // Switch to the "Results" tab
        tabPane.getSelectionModel().select(resultsTab);

        // Start the simulation
        try {
            EndSimulationData endSimulationData = engine.runSimulation();
            System.out.println("The simulation ended after " + endSimulationData.getEndConditionVal() + " " +
                    endSimulationData.getEndCondition());
            System.out.println("Run ID: " + endSimulationData.getRunId() + "\n");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

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
                                try {
                                    engine.setEnvironmentVariable(variable.getName(), newValue);
                                } catch (IncompatibleType e) {
                                    throw new RuntimeException(e);
                                }
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
                                variable.setValue(newValue); // todo: add exceptions
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
                            textField.setPrefHeight(10);
                            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                                // Update the variable's value
                                variable.setValue(newValue);  // todo: add exceptions
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
                setModel(engine);
            }catch (Exception e){
                //צריך לבדוק כאן איך מתמודדים עם שגיאות
            }
            detailsPageComponentController.setWorldDetailsTree();
            setEnvironmentTable(); // gon
            setEntityTable(); // gon
        }
    }

    @FXML
    void showWorldTree(ActionEvent event) {


    }

    public void setModel(EngineInterface engine) {
        detailsPageComponentController.setModel(engine);
        //resultsPageComponentController.setModel(engine);
    }

    @FXML
    public void initialize() {
        if (detailsPageComponentController != null) {
            detailsPageComponentController.setMainController(this);
            //resultsPageComponentController.setMainController(this);
        }
    }

    public void setDetailsPageController(DetailsPageController detailsPageController) {
        this.detailsPageComponentController = detailsPageController;
        detailsPageController.setMainController(this);
    }

}
