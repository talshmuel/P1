package UI.page.results.done;

import UI.page.results.ResultsPageController;
import data.transfer.object.run.result.EntityResultInfo;
import data.transfer.object.run.result.RunResultInfo;
import data.transfer.object.run.result.WorldResultInfo;
import engine.EngineInterface;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.*;


public class DoneResultPageController {
    private EngineInterface engine;
    private ResultsPageController mainController;

    int runIdCurrentDisplaying;
    @FXML
    private LineChart<?, ?> entitiesAmountChart;

    @FXML
    private Label runIDLabel;

    @FXML
    private Label startTimeLabel;

    @FXML
    private TextField avgValTextField;

    @FXML
    private TextField consistencyTextField;


    @FXML
    private MenuButton entitiesMenuButton;

    @FXML
    private MenuButton propertiesMenuButton;


    @FXML
    private Label avgErrorLabel;
    @FXML
    private BarChart<?, ?> propertyHistogramBarChart;

    @FXML
    void handleOnClickRerunButton(ActionEvent event) {
        mainController.rerunSpecificSimulation(runIdCurrentDisplaying);
    }

    public void displayDoneSimulationResults() {
        RunResultInfo runResult = engine.displayRunResultsInformation(runIdCurrentDisplaying);
        if(Objects.equals(runResult.getCurrentState(), "CANCELLED")){
            Platform.runLater(()->{
                Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                errorAlert.setContentText("Cancellation Reason: "+runResult.getCancelReason());
                errorAlert.show();
            });
        }
        updateRunIDAndStartTimeLabels(runResult);
        updateSimulationRuntimeComponents(runResult);

    }

    void updateSimulationRuntimeComponents(RunResultInfo runtimeResult){
        displayEntitiesAmountChart(runtimeResult);
        showEntitiesMenu(runtimeResult);

    }
    public void showEntitiesMenu(RunResultInfo runtimeResult) {
        Platform.runLater(()->{
            entitiesMenuButton.getItems().clear();
        });
        WorldResultInfo worldResult = runtimeResult.getWorldResultAtEveryTick().get(0);
        worldResult.getAllEntities().forEach((entName, entArr) -> {
            MenuItem itemToAdd = new MenuItem(entName);
            itemToAdd.setOnAction((event) -> {
                handleEntityChoiceOnMenuButton(runtimeResult, entName);
            });
            Platform.runLater(() -> {
                entitiesMenuButton.getItems().addAll(itemToAdd);
            });

        });


    }
    public void handleEntityChoiceOnMenuButton(RunResultInfo runtimeResult, String entName){

        Platform.runLater(()->{
            entitiesMenuButton.setText(entName);
            propertiesMenuButton.getItems().clear();
            avgErrorLabel.setText("");

        });
        ArrayList<EntityResultInfo> entities = runtimeResult.getWorldResultAtEveryTick().get(0).getAllEntities().get(entName);
        if(entities.size()!=0) {
            entities.get(0).getProperties().forEach((propName, prop) -> {
                MenuItem itemToAdd = new MenuItem(propName);
                itemToAdd.setOnAction((event) -> {
                    handlePropertyChoiceOnMenuButton(runtimeResult, entName, propName);
                });
                Platform.runLater(() -> {
                    propertiesMenuButton.getItems().addAll(itemToAdd);
                });
            });
            Platform.runLater(() -> {
                propertiesMenuButton.setDisable(false);
                propertiesMenuButton.fire();
            });

        }
        else{
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
            errorAlert.setContentText("No instances of the entity \""+entName+"\" at all");
            errorAlert.show();
        }

    }

    public void handlePropertyChoiceOnMenuButton(RunResultInfo runtimeResult, String entName, String propName){
        int indexOfLastResult = runtimeResult.getWorldResultAtEveryTick().size();
        WorldResultInfo worldResult = runtimeResult.getWorldResultAtEveryTick().get(indexOfLastResult-1);

        double consistency = calcConsistencyOfProp(runtimeResult, entName, propName);
        double average = calcAverageOfProp(worldResult, entName, propName);
        Map<Object, Integer> histogram = createPropertyHistogram(worldResult, entName, propName);

        XYChart.Series series = new XYChart.Series<>();
        histogram.forEach((propval, numOfEntitiesInThisVal)->{
            series.getData().add(new XYChart.Data(String.valueOf(propval), numOfEntitiesInThisVal));
        });
        series.setName(propName);

        Platform.runLater(()->{
            propertyHistogramBarChart.getData().clear();
            propertiesMenuButton.setText(propName);
            avgValTextField.setText(String.valueOf(average));
            consistencyTextField.setText(String.valueOf(consistency));
            propertyHistogramBarChart.getData().add(series);
        });
    }
    public double calcConsistencyOfProp(RunResultInfo runResult, String entName, String propName) {
        double lastAdded = 0;
        int numOfWorldResults = runResult.getWorldResultAtEveryTick().size();
        ArrayList<Double> ticksThatPropChanged = new ArrayList<>();
        int i= 0;
        WorldResultInfo worldResult = runResult.getWorldResultAtEveryTick().get(i++);
        while (i<numOfWorldResults && worldResult.getAllEntities().get(entName).size() != 0) {

            double toAdd = worldResult.getAllEntities().get(entName).get(0).getProperties().get(propName).getTickNumThatHasChanged();
            if (toAdd - lastAdded != 0)
                ticksThatPropChanged.add(toAdd - lastAdded);

            lastAdded = toAdd;
            worldResult = runResult.getWorldResultAtEveryTick().get(i++);

        }

        return getAverage(ticksThatPropChanged);
    }
    public double calcAverageOfProp(WorldResultInfo worldResult, String entName, String propName){
        ArrayList<EntityResultInfo> entities = worldResult.getAllEntities().get(entName);
        if(entities.size()!=0) {
            String propType = worldResult.getAllEntities().get(entName).get(0).getProperties().get(propName).getType();

            if (propType == "Float" || propType == "Integer") {
                ArrayList<Double> propVals = new ArrayList<>();
                worldResult.getAllEntities().get(entName).forEach((ent) -> {
                    propVals.add((Double) ent.getProperties().get(propName).getValue());
                });
                return getAverage(propVals);
            }
            else {
                Platform.runLater(()->{
                    avgErrorLabel.setText("\""+propName+"\" is not a number type");
                });
            }

        }
        else{
            Platform.runLater(()->{
                avgErrorLabel.setText("No \""+entName+"\" entities at end of simulation");
            });

        }
        return 0;
    }
    public double getAverage(ArrayList<Double> numbers) {
        if (numbers.isEmpty()) {
            return 0.0; // Return 0 if the ArrayList is empty to avoid division by zero.
        }
        int sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        return (double) sum / numbers.size();
    }
    public void displayEntitiesAmountChart(RunResultInfo runtimeResult){
        Platform.runLater(()->{
            entitiesAmountChart.getData().clear();
        });
        runtimeResult.getWorldResultAtEveryTick().get(0).getAllEntities().forEach((entName, entResultArr)->{
            XYChart.Series series = new XYChart.Series<>();
            int i=0;
            while (i<runtimeResult.getWorldResultAtEveryTick().size()){
                series.getData().add(new XYChart.Data(String.valueOf(i), runtimeResult.getWorldResultAtEveryTick().get(i).getAllEntities().get(entName).size()));
                i+=1000;
            }
            series.setName(entName);


            Platform.runLater(() -> {
                entitiesAmountChart.getData().add(series);

            });
        });
    }

    void updateRunIDAndStartTimeLabels(RunResultInfo runtimeResult){
        Platform.runLater(() -> {
            startTimeLabel.setText(MillisecondsToFormattedString(runtimeResult.getStartTime()));
            runIDLabel.setText(runtimeResult.getRunID() + "   ");
        });
    }
    public String MillisecondsToFormattedString(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy | HH.mm.ss");
        Date date = new Date(time);
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public void setRunIdCurrentDisplaying(int runIdCurrentDisplaying) {
        this.runIdCurrentDisplaying = runIdCurrentDisplaying;
    }
    public Map<Object, Integer> createPropertyHistogram(WorldResultInfo worldResult, String entityName, String propName){
        Map<Object, Integer> res = new HashMap<>();
        for(EntityResultInfo entity : worldResult.getAllEntities().get(entityName)) {
            Object propVal = entity.getProperties().get(propName).getValue();
            Integer numOfEntities = res.get(propVal);
            if (numOfEntities == null)
                numOfEntities = 0;
            res.put(propVal, ++numOfEntities);

        }
        return res;
    }

    public void setMainController(ResultsPageController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }

}
