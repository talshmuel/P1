package UI.page.execution;

import UI.PRDController;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import data.transfer.object.EndSimulationData;
import data.transfer.object.DataFromUser;
import data.transfer.object.definition.*;
import engine.EngineInterface;
import exception.IncompatibleType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import world.property.impl.Property;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionPageController {
    private EngineInterface engine;
    private PRDController mainController;
    DataFromUser dataFromUser;
    ExecutorService threadExecutor = Executors.newFixedThreadPool(3);
    public ExecutionPageController(){
        dataFromUser = new DataFromUser();
    }

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
        envNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        envTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        envValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        populateEnvironmentTableWithValues();
        System.out.println("now going to setEnvironmentValueCell");
        //setRandomValuesInTable();
        setEnvironmentValueCell();
    }

    public void populateEnvironmentTableWithValues(){
        System.out.println("populateEnvironmentTableWithValues");
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        ObservableList<EnvironmentTableView> environmentVariables = FXCollections.observableArrayList();

        System.out.println("Environment variables:");
        for (PropertyInfo p : definitions) {
            environmentVariables.add(new EnvironmentTableView(p.getName(), p.getType(), p.getBottomLimit(), p.getTopLimit()));
        }

//        System.out.println("Environment values:");
//        ArrayList<PropertyValueInfo> environmentValues = engine.getEnvironmentValues();
//        int i=0;
//        for(PropertyValueInfo v : environmentValues){
//            environmentVariables.get(i++).setValue(v.getVal());
//        }
//        System.out.println("#################3:");

        envTable.setItems(environmentVariables);
    }


//    public void setRandomValuesInTable(){
//        System.out.println("setRandomValuesInTable");
//        envValueCol.setCellFactory(column -> new TableCell<EnvironmentTableView, String>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                System.out.println("updateItem");
//                super.updateItem(item, empty);
//
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    EnvironmentTableView variable = (EnvironmentTableView) getTableRow().getItem();
//                    Node inputNode;
//
//                    switch (variable.getType()) {
//                        case "Integer": {
//                            inputNode = setRandomIntegerEnvironmentCell(variable);
//                            break;
//                        }
//                        case "Float": {
//                            inputNode = setRandomFloatEnvironmentCell(variable);
//                            break;
//                        }
//                        case "Boolean": {
//                            inputNode = setRandomBooleanEnvironmentCell(variable);
//                            break;
//                        }
//                        default: { // String type
//                            inputNode = setRandomStringEnvironmentCell(variable);
//                            break;
//                        }
//                    }
//
//                    setGraphic(inputNode);
//                }
//            }
//        });
//    }
//
//    public HBox setRandomIntegerEnvironmentCell(EnvironmentTableView variable){
//        TextField inputField = new TextField();
//
//        Integer bottomLimit = (Integer) variable.getBottomLimit();
//        Integer topLimit = (Integer) variable.getTopLimit();
//
//        inputField.setText(variable.getValue().toString());
//
//        Label label = new Label(bottomLimit + " - " + topLimit);
//        HBox hbox = new HBox(label, inputField);
//        hbox.setAlignment(Pos.CENTER);
//        return hbox;
//    }
//
//    public HBox setRandomFloatEnvironmentCell(EnvironmentTableView variable){
//        TextField inputField = new TextField();
//
//        Double bottomLimit = (Double) variable.getBottomLimit();
//        Double topLimit = (Double) variable.getTopLimit();
//
//        inputField.setText(variable.getValue().toString());
//
//        Label label = new Label(bottomLimit + " - " + topLimit);
//        HBox hbox = new HBox(label, inputField);
//        hbox.setAlignment(Pos.CENTER);
//        return hbox;
//    }
//
//    public TextField setRandomStringEnvironmentCell(EnvironmentTableView variable){
//        TextField textField = new TextField();
//        textField.setPrefHeight(10);
//        textField.setText(variable.getValue().toString());
//        return textField;
//    }
//
//    public HBox setRandomBooleanEnvironmentCell(EnvironmentTableView variable){
//        CheckBox trueCheckBox = new CheckBox("True");
//        CheckBox falseCheckBox = new CheckBox("False");
//        trueCheckBox.setSelected(false);
//        falseCheckBox.setSelected(false);
//
//        if(variable.getValue().equals(true)){
//            trueCheckBox.setSelected(true);
//        } else {
//            falseCheckBox.setSelected(true);
//        }
//
//        HBox hbox = new HBox(trueCheckBox, falseCheckBox);
//        hbox.setAlignment(Pos.CENTER);
//        return hbox;
//    }


/////////////////////////////////////////////////////////////////
    public void setEnvironmentValueCell(){
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

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

    public HBox setEnvironmentIntegerCell(EnvironmentTableView variable){
        TextField inputField = new TextField();

        Integer bottomLimit = (Integer) variable.getBottomLimit();
        Integer topLimit = (Integer) variable.getTopLimit();

        inputField.setOnAction(event -> {
            String input = inputField.getText();

            try {
                double inputValue = Double.parseDouble(input);

                if (inputValue >= bottomLimit && inputValue <= topLimit) {
                    variable.setValue(inputValue);
                    dataFromUser.setEnvironment(variable.getName(), inputValue);
                } else {
                    showErrorAlert("Invalid input! Please enter a number between " + bottomLimit + " and " + topLimit + ".");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input! Please enter a number.");
            }
        });

        Label label = new Label(bottomLimit + " - " + topLimit);
        HBox hbox = new HBox(label, inputField);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }


    public HBox setEnvironmentFloatCell(EnvironmentTableView variable){
        TextField inputField = new TextField();

        Double bottomLimit = (Double) variable.getBottomLimit();
        Double topLimit = (Double) variable.getTopLimit();

        inputField.setOnAction(event -> {
            String input = inputField.getText();

            try {
                double inputValue = Double.parseDouble(input);

                if (inputValue >= bottomLimit && inputValue <= topLimit) {
                    variable.setValue(inputValue);
                    dataFromUser.setEnvironment(variable.getName(), inputValue);
                } else {
                    showErrorAlert("Invalid input! Please enter a number between " + bottomLimit + " and " + topLimit + ".");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input! Please enter a number.");
            }
        });

        Label label = new Label(bottomLimit + " - " + topLimit);
        HBox hbox = new HBox(label, inputField);
        hbox.setAlignment(Pos.CENTER);
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
        falseCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(true));
        dataFromUser.setEnvironment(variable.getName(), true);
    }

    public void falseBoxChecked(EnvironmentTableView variable, CheckBox trueCheckBox, CheckBox falseCheckBox) {
        trueCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(false));
        dataFromUser.setEnvironment(variable.getName(), false);
    }

    public TextField setEnvironmentStringCell(EnvironmentTableView variable){
        TextField textField = new TextField();
        textField.setPrefHeight(10);
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String newValue = textField.getText();
                variable.setValue(newValue);
                dataFromUser.setEnvironment(variable.getName(), newValue);
            }
        });
        return textField;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setEntityTable(){
        entityNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        entityPopCol.setCellValueFactory(new PropertyValueFactory<>("population"));
        populateEntityTableWithValues();
        setPopulationCell();
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
                            dataFromUser.setPopulation(entity.getName(),Integer.parseInt(newValue));
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
        threadExecutor.execute(this::runSimulation);

        // switch to the "Results" tab
        //tabPane.getSelectionModel().select(resultsTab);
    }

    private synchronized void runSimulation(){
        try {
            EndSimulationData endSimulationData = engine.runSimulation(new DataFromUser(dataFromUser));
            //dataFromUser.cleanup();
            Platform.runLater(()->{
                mainController.addUpdateSimulationToResultsPage("The simulation ended after " + endSimulationData.getEndConditionVal() + " " +
                    endSimulationData.getEndCondition()+"\nRun ID: " + endSimulationData.getRunId() + "\n");
            });


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
