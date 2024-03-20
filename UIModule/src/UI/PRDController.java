package UI;

import UI.page.details.DetailsPageController;
import UI.page.execution.ExecutionPageController;
import UI.page.results.ResultsPageController;
import data.transfer.object.definition.SimulationInfo;
import engine.Engine;
import engine.EngineInterface;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;


public class PRDController {
    @FXML private SplitPane detailsPageComponent;
    @FXML private DetailsPageController detailsPageComponentController;
    @FXML private GridPane executionPageComponent;
    @FXML private ExecutionPageController executionPageComponentController;
    @FXML private SplitPane resultsPageComponent;
    @FXML private ResultsPageController resultsPageComponentController;
    @FXML private TabPane tabPane;
    @FXML private Tab newExecutionTab;
    @FXML private Tab resultsTab;
    private EngineInterface engine;
    @FXML private TextField filePathField;


    @FXML private TextField numberOfPendingTextField;
    @FXML private TextField numberOfRunningTextField;
    @FXML private TextField numberOfDoneTextField;

    ExecutorService managementQueueExecutor = Executors.newFixedThreadPool(1);
    private SimpleStringProperty selectedFileProperty;
    public PRDController(){
        engine = new Engine();
        selectedFileProperty = new SimpleStringProperty();



    }
    private void updateQueueData(){
        managementQueueExecutor.execute(()->{
            while (true){
                if(resultsPageComponentController != null) {
                    int numOfPending = resultsPageComponentController.getNumOfPending();
                    int numOfRunning = resultsPageComponentController.getNumOfRunning();
                    int numOfDone = resultsPageComponentController.getNumOfDone();

                    Platform.runLater(() -> {
                        numberOfPendingTextField.setText(String.valueOf(numOfPending));
                        numberOfRunningTextField.setText(String.valueOf(numOfRunning));
                        numberOfDoneTextField.setText(String.valueOf(numOfDone));
                    });
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void addNewRunResultToResultsPage(int id){
        resultsPageComponentController.addNewRunResult(id);
    }

    @FXML
    void openFileChooser(ActionEvent event) {
        cleanup();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        ); // added
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedFileProperty.set(selectedFile.getAbsolutePath());
            filePathField.appendText(selectedFileProperty.get());
            try {
                engine.createSimulationByXMLFile(selectedFileProperty.get());

                SimulationInfo s = engine.displaySimulationDefinitionInformation();
                int t = s.getNumOfThreads();
                resultsPageComponentController.setNumOfRunStateThreads(engine.displaySimulationDefinitionInformation().getNumOfThreads());

                setModel(engine);

                detailsPageComponentController.setWorldDetailsTree();
                executionPageComponentController.setEnvironmentTable();
                executionPageComponentController.setEntityTable();
                updateQueueData();
            } catch (Exception e){
                showErrorAlert(e.getMessage());
            }
        }
    }

        private void cleanup(){
        resultsPageComponentController.cleanup();
        executionPageComponentController.cleanup();
        engine.cleanup();
        numberOfPendingTextField.clear();
        numberOfRunningTextField.clear();
        numberOfDoneTextField.clear();

    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public void setModel(EngineInterface engine) {
        detailsPageComponentController.setModel(engine);
        executionPageComponentController.setModel(engine);
        resultsPageComponentController.setModel(engine);
    }

    @FXML
    public void initialize() {
        filePathField.textProperty().bind(selectedFileProperty);
        if (detailsPageComponentController != null) {
            detailsPageComponentController.setMainController(this);
            executionPageComponentController.setMainController(this);
            resultsPageComponentController.setMainController(this);

        }
    }
    public void rerunSpecificSimulation(int runID) {
        tabPane.getSelectionModel().select(newExecutionTab);
        executionPageComponentController.handleRerunSpecificRun(runID);
    }
    public void switchToResultsTab(){
        tabPane.getSelectionModel().select(resultsTab);
    }



}


