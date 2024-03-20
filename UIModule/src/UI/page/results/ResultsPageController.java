package UI.page.results;

import UI.PRDController;
import UI.page.results.done.DoneResultPageController;
import UI.page.results.running.RunningResultPageController;
import engine.EngineInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ResultsPageController {
    private EngineInterface engine;
    private PRDController mainController;
    @FXML private BorderPane runningResultsPage;
    @FXML private RunningResultPageController runningResultsPageController;
    @FXML private BorderPane doneResultsPage;
    @FXML private DoneResultPageController doneResultsPageController;
    @FXML StackPane resultStackPane;

    @FXML
    private ListView<SimulationResult> resultsListView;
    ExecutorService updateRunStateExecutor;
    ExecutorService runtimeResultsExecutor = Executors.newFixedThreadPool(1);
    int runIdCurrentDisplaying;

    public void setNumOfRunStateThreads(int threads){
        updateRunStateExecutor = Executors.newFixedThreadPool(threads);
    }


    public void cleanup(){
        resultStackPane.getChildren().clear();
        resultsListView.getItems().clear();
        runIdCurrentDisplaying = 1;

    }
    @FXML
    private void handleClickOnResult() {
        SimulationResult selected = resultsListView.getSelectionModel().getSelectedItem();
        updateCurrentDisplaying(selected.getId());
        if(selected.currentState== SimulationResult.SimulationState.RUNNING ||
                selected.currentState== SimulationResult.SimulationState.PAUSED){

            resultStackPane.getChildren().setAll(runningResultsPage);
            runtimeResultsExecutor.execute(runningResultsPageController::displayRunningSimulationResults);
        }
        else if(selected.currentState== SimulationResult.SimulationState.DONE ||
                selected.currentState== SimulationResult.SimulationState.CANCELLED){
            resultStackPane.getChildren().setAll(doneResultsPage);
            runtimeResultsExecutor.execute(doneResultsPageController::displayDoneSimulationResults);
        }
    }


    void updateCurrentDisplaying(int runID){
        runIdCurrentDisplaying = runID;
        runningResultsPageController.setRunIdCurrentDisplaying(runIdCurrentDisplaying);
        doneResultsPageController.setRunIdCurrentDisplaying(runIdCurrentDisplaying);
        synchronized (runningResultsPageController){//if there is thread that is in "wait" (because of PAUSE) we need to wake up this thread by notify
            runningResultsPageController.notifyAll();
        }
    }

    public void switchToDoneResultPage () {
        Platform.runLater(() -> {
            resultStackPane.getChildren().setAll(doneResultsPage);
        });
    }

    public void addNewRunResult(int runID){
        SimulationResult newResult = new SimulationResult(runID);
        resultsListView.getItems().add(newResult);
        updateRunState(runID);
    }
    public void updateRunState(int runID) {
        updateRunStateExecutor.execute(() -> {
            try {
                sleep(1000);// כדי לאפשר לסימולציה להתחיל
                SimulationResult resultToUpdate = findResult(runID);
                String currState, oldState = "PENDING";
                do {
                    currState = engine.getCurrentStateOfSpecificRun(runID);
                    String finalCurrState = currState;//for the lambda (lambda get just final)
                    if(!Objects.equals(currState, oldState)) {
                        Platform.runLater(() -> {
                            resultToUpdate.setCurrentState(convertStateStringToEnum(finalCurrState));
                            resultsListView.refresh();//to update the screen view
                        });
                    }

                    if(Objects.equals(oldState, "PAUSED") && !Objects.equals(currState, "PAUSED")){
                        synchronized (runningResultsPageController) {
                            runningResultsPageController.notifyAll(); //הטרד המציג את נתוני הריצה בזמן אמת נמצא בWAIT כאשר הסימולציה בPAUSE ולכן כשהסטייט משתנה מPAUSE לRUNNING או DONE יש להודיע על כך
                        }
                    }
                    oldState = currState;

                } while (!Objects.equals(currState, "DONE") && !Objects.equals(currState, "CANCELLED"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    SimulationResult findResult(int runID){
        for(SimulationResult result : resultsListView.getItems()){
            if(result.getId()==runID)
                return result;
        }
        return null;
    }

    public SimulationResult.SimulationState convertStateStringToEnum(String strState){
        switch (strState){
            case "PENDING":
                return SimulationResult.SimulationState.PENDING;
            case "RUNNING":
                return SimulationResult.SimulationState.RUNNING;
            case "PAUSED":
                return SimulationResult.SimulationState.PAUSED;
            case "DONE":
                return SimulationResult.SimulationState.DONE;
            case "CANCELLED":
                return SimulationResult.SimulationState.CANCELLED;
        }
        return null;
    }

    public void rerunSpecificSimulation(int runID) {
        mainController.rerunSpecificSimulation(runID);
    }

    public int getNumOfPending(){
        int res=0;
        for (SimulationResult result : resultsListView.getItems()){
            if(Objects.equals(String.valueOf(result.getCurrentState()), "PENDING"))
                res++;
        }
        return res;
    }
    public int getNumOfRunning(){
        int res=0;
        for (SimulationResult result : resultsListView.getItems()){
            if(Objects.equals(String.valueOf(result.getCurrentState()), "RUNNING") || Objects.equals(String.valueOf(result.getCurrentState()), "PAUSED"))
                res++;
        }
        return res;
    }
    public int getNumOfDone(){
        int res=0;
        for (SimulationResult result : resultsListView.getItems()){
            if(Objects.equals(String.valueOf(result.getCurrentState()), "DONE") ||Objects.equals(String.valueOf(result.getCurrentState()), "CANCELLED"))
                res++;
        }
        return res;

    }

    public void setMainController(PRDController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
        runningResultsPageController.setModel(engine);
        doneResultsPageController.setModel(engine);
    }
    @FXML
    public void initialize() {
        runningResultsPageController.setMainController(this);
        doneResultsPageController.setMainController(this);
        resultStackPane.getChildren().setAll();
    }

}
