package UI.page.execution;

import UI.PRDController;
import data.transfer.object.EndSimulationData;
import data.transfer.object.definition.*;
import engine.EngineInterface;
import exception.IncompatibleType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.Map;

public class ExecutionPageController {
    private EngineInterface engine;
    private PRDController mainController;

    @FXML private TableView<EntityInfo> entityTable;
    @FXML private TableColumn<EntityInfo, String> entityNameCol;
    @FXML private TableColumn<EntityInfo, Integer> entityPopCol;

    @FXML private TableView<EnvironmentTableView> envTable;
    @FXML private TableColumn<EnvironmentTableView, String> envNameCol;
    @FXML private TableColumn<EnvironmentTableView, String> envTypeCol;
    @FXML private TableColumn<EnvironmentTableView, String> envValueCol;
    @FXML private Button startSimulation;
    @FXML private Button clearSimulation;

    public void setEnvironmentTable() {
        System.out.println("!!!!!!!");
        envNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        envTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        envValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        populateEnvironmentTableWithValues();
        setEnvironmentValueCell();
    }
    public void populateEnvironmentTableWithValues(){
        System.out.println("!!!!populateEnvironmentTableWithValues!!!");
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        ObservableList<EnvironmentTableView> environmentVariables = FXCollections.observableArrayList();
        for (PropertyInfo p : definitions) {
            environmentVariables.add(new EnvironmentTableView(p.getName(), p.getType(), p.getBottomLimit(), p.getTopLimit()));
        }
        envTable.setItems(environmentVariables);
    }

    public void setEnvironmentValueCell(){
        System.out.println("!!!!setEnvironmentValueCell!!!");

        envValueCol.setCellFactory(column -> new TableCell<EnvironmentTableView, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    EnvironmentTableView variable = (EnvironmentTableView) getTableRow().getItem();
                    Node inputNode;

                    switch (variable.getType()) {
                        case "Integer": {
                            inputNode = setEnvironmentIntegerCell(variable);
                            break;
                        }
                        case "Float": {
                            inputNode = setEnvironmentFloatCell(variable);
                            break;
                        }
                        case "Boolean": {
                            inputNode = setEnvironmentBooleanCell(variable);
                            break;
                        }
                        default: { // String type
                            inputNode = setEnvironmentStringCell(variable);
                            break;
                        }
                    }
                    setGraphic(inputNode);
                }
            }
        });
    }
    public HBox setEnvironmentIntegerCell(EnvironmentTableView variable){
        // todo: setOnAction for the slider or whatever we choose
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
                // todo: add exceptions
            }
        });
        return hbox;
    }
    public HBox setEnvironmentFloatCell(EnvironmentTableView variable){
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
            try {
                engine.setEnvironmentVariable(variable.getName(), newValue);
            } catch (IncompatibleType e) {
                // todo: add exceptions
            }
        });
        return hbox;
    }
    public HBox setEnvironmentBooleanCell(EnvironmentTableView variable){
        CheckBox trueCheckBox = new CheckBox("True");
        CheckBox falseCheckBox = new CheckBox("False");

        trueCheckBox.setSelected(false);
        falseCheckBox.setSelected(false);

        //trueBoxChecked(variable, trueCheckBox, falseCheckBox);
        //falseBoxChecked(variable, trueCheckBox, falseCheckBox);
        trueCheckBox.setOnAction(event -> trueBoxChecked(variable, trueCheckBox, falseCheckBox));
        falseCheckBox.setOnAction(event -> falseBoxChecked(variable, trueCheckBox, falseCheckBox));

        HBox hbox = new HBox(trueCheckBox, falseCheckBox);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }
    public void trueBoxChecked(EnvironmentTableView variable, CheckBox trueCheckBox, CheckBox falseCheckBox){
//        trueCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                falseCheckBox.setSelected(false);
//                variable.setValue(Boolean.toString(true));
//                try {
//                    engine.setEnvironmentVariable(variable.getName(), true);
//                } catch (IncompatibleType e) {
//                    // todo: add exceptions
//                }
//            } else if (!falseCheckBox.isSelected()) {
//                trueCheckBox.setSelected(true); // enforce selecting one
//            }
//        });
        falseCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(true));
        try {
            System.out.println("$$ true!");
            engine.setEnvironmentVariable(variable.getName(), true);
        } catch (IncompatibleType e) {
            // todo: add exceptions
        }
    }
    public void falseBoxChecked(EnvironmentTableView variable, CheckBox trueCheckBox, CheckBox falseCheckBox){
//        falseCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                trueCheckBox.setSelected(false);
//                variable.setValue(Boolean.toString(false));
//                try {
//                    engine.setEnvironmentVariable(variable.getName(), false);
//                } catch (IncompatibleType e) {
//                    // todo: add exceptions
//                }
//            } else if (!trueCheckBox.isSelected()) {
//                falseCheckBox.setSelected(true); // enforce selecting one
//            }
//        });
        trueCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(false));
        try {
            System.out.println("$$ false!");
            engine.setEnvironmentVariable(variable.getName(), false);
        } catch (IncompatibleType e) {
            // todo: add exceptions
        }
    }

    public TextField setEnvironmentStringCell(EnvironmentTableView variable){
        TextField textField = new TextField();
        textField.setPrefHeight(10);
//        textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            variable.setValue(newValue);
//            try {
//                engine.setEnvironmentVariable(variable.getName(), newValue);
//            } catch (IncompatibleType e) {
//                // todo: add exceptions
//            }
//        });
        ///////////////////////////////////////////////
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String newValue = textField.getText();
                variable.setValue(newValue);

                System.out.println("%%%%");

                try {
                    System.out.println("new value: " + newValue);
                    System.out.println("@@@@@");
                    engine.setEnvironmentVariable(variable.getName(), newValue);
                    System.out.println("new value: " + newValue);
                } catch (IncompatibleType e) {
                    // todo: add exceptions
                }
                // Additional action or navigation logic here...
            }
        });
        /////////////////////////////////////////////////
        return textField;
    }


    public void setEntityTable(){
        entityNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        entityPopCol.setCellValueFactory(new PropertyValueFactory<>("population"));
        populateEntityTableWithValues();
        setPopulationCell();
        // todo: add population to world's entity
    }

    public void setPopulationCell(){
        entityPopCol.setCellFactory(column -> new TableCell<EntityInfo, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    EntityInfo entity = getTableView().getItems().get(getIndex());
                    TextField textField = new TextField();
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

    public void populateEntityTableWithValues(){
        ArrayList<EntityInfo> entities = engine.displaySimulationDefinitionInformation().getEntities();
        for(EntityInfo e : entities){
            entityTable.getItems().add(new EntityInfo(e.getName(), 0, null));
        }
        ObservableList<EntityInfo> entityData = FXCollections.observableArrayList(entities);
        entityTable.setItems(entityData);
    }

    @FXML void handleStartSimulation(ActionEvent event) {
        try {
            EndSimulationData endSimulationData = engine.runSimulation();
            System.out.println("The simulation ended after " + endSimulationData.getEndConditionVal() + " " +
                    endSimulationData.getEndCondition());
            System.out.println("Run ID: " + endSimulationData.getRunId() + "\n");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        // switch to the "Results" tab
        //tabPane.getSelectionModel().select(resultsTab);
    }

    @FXML void handleClearSimulation(ActionEvent event) {
        // todo: למחוק רק את הערכים גון!!!!!
        envTable.getItems().clear();
        entityTable.getItems().clear();
        engine.cleanResults();
    }

    public void setMainController(PRDController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }
}
