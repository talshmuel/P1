package UI.page.execution;

import UI.PRDController;
import data.transfer.object.DataFromUser;
import data.transfer.object.definition.*;
import engine.EngineInterface;
import exception.SimulationRunningException;
import javafx.animation.*;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

public class ExecutionPageController {
    EngineInterface engine;
    PRDController mainController;
    ArrayList<DataFromUser> dataFromUserForEachRun;

    int nextRunID;

    @FXML private TableView<EntityTableView> entityTable;
    @FXML private TableColumn<EntityTableView, String> entityNameCol;
    @FXML private TableColumn<EntityTableView, Integer> entityPopCol;
    @FXML private TableColumn<EntityTableView, Integer> entitySelectedCol;

    @FXML private TableView<EnvironmentTableView> envTable;
    @FXML private TableColumn<EnvironmentTableView, String> envNameCol;
    @FXML private TableColumn<EnvironmentTableView, String> envTypeCol;
    @FXML private TableColumn<EnvironmentTableView, String> envValueCol;
    @FXML private TableColumn<EnvironmentTableView, String> envSelectedValCol;

    @FXML private Button startSimulation;
    @FXML private Button clearSimulation;

    @FXML private CheckBox cancelAnimation;
    boolean animationActivated;

    @FXML private GridPane mainGridPane;

    @FXML private ScrollBar sideScroller;
    @FXML private ScrollBar downScroller;


    public ExecutionPageController(){
        dataFromUserForEachRun = new ArrayList<>();
        nextRunID=0;
        dataFromUserForEachRun.add(new DataFromUser(++nextRunID));
        animationActivated = true;
    }
    public void cleanup() {
        dataFromUserForEachRun.clear();
        nextRunID=0;
        dataFromUserForEachRun.add(new DataFromUser(++nextRunID));
    }

    public void initialize() {
        sideScroller.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainGridPane.setTranslateY(-newValue.doubleValue());
        });

        downScroller.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainGridPane.setTranslateX(-newValue.doubleValue());
        });

    }



    public void setEnvironmentTable() {
        envNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        envTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        envValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        envSelectedValCol.setCellValueFactory(new PropertyValueFactory<>("selectedValue"));
        populateEnvironmentTableWithValues(null);
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

    public void populateEnvironmentTableWithValues(Map<String, Object> environmentData) {
        ArrayList<PropertyInfo> definitions = engine.getEnvironmentDefinitions();
        ObservableList<EnvironmentTableView> environmentVariables = FXCollections.observableArrayList();
        for (PropertyInfo p : definitions) {
            String selectedValue;

            if (environmentData != null && !environmentData.isEmpty()) {
                selectedValue = (environmentData.get(p.getName())).toString();
            } else {
                selectedValue = findRandomValue(p.getName());
            }

            environmentVariables.add(new EnvironmentTableView(p.getName(), p.getType(), p.getBottomLimit(), p.getTopLimit(), selectedValue));
        }
        Platform.runLater(() -> {
            envTable.setItems(environmentVariables);
        });
    }


    public void setEnvironmentValueCell(){
        Platform.runLater(() -> {
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
                    dataFromUserForEachRun.get(nextRunID-1).setEnvironment(variable.getName(), inputValue);
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
                    dataFromUserForEachRun.get(nextRunID-1).setEnvironment(variable.getName(), inputValue);
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
        dataFromUserForEachRun.get(nextRunID-1).setEnvironment(variable.getName(), true);
    }
    public void falseBoxChecked(EnvironmentTableView variable, CheckBox trueCheckBox, CheckBox falseCheckBox) {
        trueCheckBox.setSelected(false);
        variable.setValue(Boolean.toString(false));
        variable.setSelectedValue(Boolean.toString(false));
        envTable.refresh();
        dataFromUserForEachRun.get(nextRunID-1).setEnvironment(variable.getName(), false);
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
                dataFromUserForEachRun.get(nextRunID-1).setEnvironment(variable.getName(), inputValue);
            }
        });
        variable.setSelectedValue(initialSelectedValue);
        return inputField;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setEntityTable(){
        entityNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        entityPopCol.setCellValueFactory(new PropertyValueFactory<>("population"));
        entitySelectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        populateEntityTableWithValues(null);
        setPopulationCell();
    }
    public void populateEntityTableWithValues(Map<String, Integer> populationData){
        ArrayList<EntityInfo> entities = engine.displaySimulationDefinitionInformation().getEntities();
        ObservableList<EntityTableView> entityVariables = FXCollections.observableArrayList();

        for(EntityInfo e : entities) {
            Integer population=0;
            if(populationData != null){
                population = populationData.get(e.getName());
            }

            entityVariables.add(new EntityTableView(e.getName(), 0, population));
        }

        Platform.runLater(() -> {
            entityTable.setItems(entityVariables);
        });
    }
    public void setPopulationCell(){
        entityPopCol.setCellFactory(column -> new TableCell<EntityTableView, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    EntityTableView variable = (EntityTableView) getTableRow().getItem();

                    if(variable != null){
                        Node inputNode = setPopulationFromUser(variable);
                        setGraphic(inputNode);
                    }
                }
            }
        });
    }

    public TextField setPopulationFromUser(EntityTableView variable){
        TextField inputField = new TextField();
        int selectedColumnSum = entityTable.getItems().stream().mapToInt(EntityTableView::getSelected).sum();

        Platform.runLater(() -> {
            inputField.setMaxWidth(100.0);
            inputField.setAlignment(Pos.CENTER);

            inputField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    String inputValue = inputField.getText();

                    if(!inputValue.matches("[0-9]+")){
                        showErrorAlert("Invalid input! Please enter a number.");

                    } else {
                        Integer newPopulation = Integer.parseInt(inputValue);
                        int populationCounter=selectedColumnSum;

                        if(!(variable.getSelected().equals(0)))
                            populationCounter -= variable.getSelected();


                        if(populationCounter+newPopulation > engine.displaySimulationDefinitionInformation().getGridSize()){
                            showErrorAlert("You have reached maximum population size: " + engine.displaySimulationDefinitionInformation().getGridSize() + "\n" +
                                    "Please choose a smaller number.");
                            //entityTable.refresh();
                        } else {
                            variable.setPopulation(newPopulation);
                            variable.setSelected(newPopulation);
                            entityTable.refresh();

                            // update value of population in engine
                            dataFromUserForEachRun.get(nextRunID-1).setPopulation(variable.getName(), newPopulation);
                        }
                    }
                }
            });
        });

        return inputField;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    @FXML void handleStartSimulation(ActionEvent startEvent) {
        mainController.addNewRunResultToResultsPage(nextRunID);
        engine.runSimulation(dataFromUserForEachRun.get(nextRunID - 1));


        dataFromUserForEachRun.add(new DataFromUser(++nextRunID));

        mainController.switchToResultsTab();
    }

    @FXML void handleClearSimulation(ActionEvent clearEvent) {

        populateEntityTableWithValues(null);

        populateEnvironmentTableWithValues(null); // reset back to random generated values
        Platform.runLater(() -> {
            entityTable.refresh();
            envTable.refresh();

        });

        cancelAnimation.setOnAction(event -> {
            animationActivated = !(cancelAnimation.isSelected());
        });
        if (animationActivated) {
            startCircleAnimation();
        }
    }

    private void startCircleAnimation(){
        Circle circle = new Circle(100, 100, 20);
        circle.setFill(Color.GREEN);

        mainGridPane.add(circle,0, 4);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    mainGridPane.getChildren().remove(circle);
                })
        );
        timeline.play();

        TranslateTransition transition = new TranslateTransition();
        transition.setNode(circle);
        transition.setByX(200);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);

        transition.setDuration(Duration.seconds(2));

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        SequentialTransition sequentialTransition = new SequentialTransition(transition, pause);

        sequentialTransition.play();
    }

    public void handleRerunSpecificRun(int runID){

        // find this specific run:
        DataFromUser data = dataFromUserForEachRun.get(runID-1);

        // update environment table:
        populateEnvironmentTableWithValues(data.getEnvironment());

        // update entities table:
        populateEntityTableWithValues(data.getPopulation());

        cancelAnimation.setOnAction(event -> {
            animationActivated = !(cancelAnimation.isSelected());
        });
        if (animationActivated) {
            startRectangleAnimation();
        }
    }

    public void setMainController(PRDController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }


    @FXML public void handleSideScroll(ScrollEvent scrollEvent) {
        double deltaY = scrollEvent.getDeltaY();
        sideScroller.setValue(sideScroller.getValue() + deltaY);
    }

    @FXML public void handleDownScroll(ScrollEvent scrollEvent) {
        double deltaY = scrollEvent.getDeltaY();
        double newValue = downScroller.getValue() - (deltaY / downScroller.getMax());
        downScroller.setValue(newValue);
    }

    public void startRectangleAnimation(){
        Rectangle rectangle = new Rectangle(50, 50, 50, 50);
        rectangle.setFill(Color.BLUE);

        mainGridPane.add(rectangle,1, 4);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    mainGridPane.getChildren().remove(rectangle);
                })
        );
        timeline.play();


        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(2));
        transition.setNode(rectangle);
        transition.setToX(400);
        rectangle.setVisible(true);
        transition.play();
    }


}