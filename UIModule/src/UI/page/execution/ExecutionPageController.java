package UI.page.execution;

import UI.PRDController;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import data.transfer.object.EndSimulationData;
import data.transfer.object.DataFromUser;
import data.transfer.object.definition.*;
import engine.EngineInterface;
import exception.IncompatibleType;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
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
import javafx.scene.layout.VBox;
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
    @FXML private TableColumn<EnvironmentTableView, String> envSelectedValCol;
    @FXML private Button startSimulation;
    @FXML private Button clearSimulation;

    public void setEnvironmentTable() {
        envNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        envTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        envValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        envSelectedValCol.setCellValueFactory(new PropertyValueFactory<>("selectedValue"));
        populateEnvironmentTableWithValues();
        setEnvironmentValueCell();

    }

    public String findRandomValue(String nameToSearch){
        ArrayList<PropertyValueInfo> environmentValues = engine.getEnvironmentValues();
        int i=0;
        for(PropertyValueInfo v : environmentValues){
            if(v.getName().equals(nameToSearch)) {
                if(v.getVal() instanceof Double){
                    return String.format("%.2f", (Double)v.getVal());
                }
                return v.getVal().toString();
            }
        }
        return null;
    }

    public void populateEnvironmentTableWithValues(){
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        ObservableList<EnvironmentTableView> environmentVariables = FXCollections.observableArrayList();
        for (PropertyInfo p : definitions) {
            String selectedValue = findRandomValue(p.getName());
            environmentVariables.add(new EnvironmentTableView(p.getName(), p.getType(), p.getBottomLimit(), p.getTopLimit(), selectedValue));
        }
        envTable.setItems(environmentVariables);
    }

    public void setEnvironmentValueCell(){
        envValueCol.setCellFactory(column -> new TableCell<EnvironmentTableView, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    EnvironmentTableView variable = (EnvironmentTableView) getTableRow().getItem();

                    if(variable != null){
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
            }
        });
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox setEnvironmentIntegerCell(EnvironmentTableView variable){
        TextField inputField = new TextField();
        inputField.setAlignment(Pos.CENTER);
        inputField.setMaxWidth(60);
        Integer bottomLimit = (Integer) variable.getBottomLimit();
        Integer topLimit = (Integer) variable.getTopLimit();
        String initialSelectedValue = variable.getSelectedValue();

        inputField.setOnAction(event -> {
            String input = inputField.getText();
            try {
                //double inputValue = Double.parseDouble(input);
                int inputValue = Integer.parseInt(input);
                if (inputValue >= bottomLimit && inputValue <= topLimit) {
                    variable.setValue(String.valueOf(inputValue));
                    variable.setSelectedValue(String.valueOf(inputValue));
                    envTable.refresh();
                    dataFromUser.setEnvironment(variable.getName(), inputValue);
                } else {
                    showErrorAlert("Invalid input! Please enter a number between " + bottomLimit + " and " + topLimit + ".");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input! Please enter a number.");
            }
        });

        Label rangeLabel = new Label("Range: " + bottomLimit + " - " + topLimit);
        VBox vbox = new VBox(rangeLabel, inputField);
        vbox.setSpacing(2);
        vbox.setAlignment(Pos.CENTER);
        variable.setSelectedValue(initialSelectedValue);
        return vbox;
    }

    public VBox setEnvironmentFloatCell(EnvironmentTableView variable){
        TextField inputField = new TextField();
        inputField.setAlignment(Pos.CENTER);
        inputField.setMaxWidth(60);
        Double bottomLimit = (Double) variable.getBottomLimit();
        Double topLimit = (Double) variable.getTopLimit();
        String initialSelectedValue = variable.getSelectedValue();

        inputField.setOnAction(event -> {
            String input = inputField.getText();
            try {
                double inputValue = Double.parseDouble(input);
                if (inputValue >= bottomLimit && inputValue <= topLimit) {
                    variable.setValue(String.valueOf(inputValue));
                    variable.setSelectedValue(String.valueOf(inputValue));
                    envTable.refresh();
                    dataFromUser.setEnvironment(variable.getName(), inputValue);
                } else {
                    showErrorAlert("Invalid input! Please enter a number between " + bottomLimit + " and " + topLimit + ".");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input! Please enter a number.");
            }
        });

        Label rangeLabel = new Label("Range: " + bottomLimit + " - " + topLimit);
        VBox vbox = new VBox(rangeLabel, inputField);
        vbox.setSpacing(2);
        vbox.setAlignment(Pos.CENTER);
        variable.setSelectedValue(initialSelectedValue);
        return vbox;
    }

    public HBox setEnvironmentBooleanCell(EnvironmentTableView variable){
        CheckBox trueCheckBox = new CheckBox("True");
        CheckBox falseCheckBox = new CheckBox("False");
        trueCheckBox.setSelected(false);
        falseCheckBox.setSelected(false);

        trueCheckBox.setOnAction(event -> trueBoxChecked(variable, trueCheckBox, falseCheckBox));
        falseCheckBox.setOnAction(event -> falseBoxChecked(variable, trueCheckBox, falseCheckBox));

        // Initialize the selected value based on the initial value
        if (variable.getValue() != null) {
            boolean initialValue = Boolean.parseBoolean(variable.getValue().toString());
            trueCheckBox.setSelected(initialValue);
            falseCheckBox.setSelected(!initialValue);
        }

        HBox hbox = new HBox(trueCheckBox, falseCheckBox);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(6);
        return hbox;
    }
    public void trueBoxChecked(EnvironmentTableView variable, CheckBox trueCheckBox, CheckBox falseCheckBox){
        falseCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(true));
        variable.setSelectedValue(Boolean.toString(true));
        envTable.refresh();
        dataFromUser.setEnvironment(variable.getName(), true);
    }

    public void falseBoxChecked(EnvironmentTableView variable, CheckBox trueCheckBox, CheckBox falseCheckBox) {
        trueCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(false));
        variable.setSelectedValue(Boolean.toString(false));
        envTable.refresh();
        dataFromUser.setEnvironment(variable.getName(), false);
    }

    public TextField setEnvironmentStringCell(EnvironmentTableView variable){
        TextField inputField = new TextField();
        inputField.setMaxWidth(100.0);
        inputField.setAlignment(Pos.CENTER);
        String initialSelectedValue = variable.getSelectedValue();

        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String inputValue = inputField.getText();
                variable.setValue(inputValue);
                variable.setSelectedValue(String.valueOf(inputValue));
                envTable.refresh();
                dataFromUser.setEnvironment(variable.getName(), inputValue);
            }
        });
        variable.setSelectedValue(initialSelectedValue);
        return inputField;
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
