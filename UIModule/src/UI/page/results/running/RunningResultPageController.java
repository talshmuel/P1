package UI.page.results.running;
import UI.page.results.ResultsPageController;
import data.transfer.object.run.result.EntityResultInfo;
import data.transfer.object.run.result.GridResultInfo;
import data.transfer.object.run.result.RunResultInfo;
import data.transfer.object.run.result.WorldResultInfo;
import engine.EngineInterface;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import world.entity.Coordinate;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

public class RunningResultPageController {
    @FXML
    private TableView<ObservableList<EntityTableItem>> currentEntitiesAmountTable;

    @FXML
    private TextField currentSecondTextField;

    @FXML
    private TextField currentTickTextField;

    @FXML
    private Label startTimeLabel;
    @FXML
    private Label runIDLabel;
    private EngineInterface engine;
    private ResultsPageController mainController;
    int runIdCurrentDisplaying;
    ObservableList<EntityTableItem> entityTableItemList;

    @FXML private GridPane entitiesGrid;

    public RunningResultPageController(){
        entityTableItemList = FXCollections.observableArrayList();
    }
    @FXML
    void handleClickOnPauseButton(ActionEvent event) {
        engine.setUserControlOnSpecificRun(runIdCurrentDisplaying, "PAUSE");

    }

    @FXML
    void handleClickOnResumeButton(ActionEvent event) {
        engine.setUserControlOnSpecificRun(runIdCurrentDisplaying, "RESUME");

    }

    @FXML
    void handleClickOnStopButton(ActionEvent event) {
        engine.setUserControlOnSpecificRun(runIdCurrentDisplaying, "STOP");

    }

    public void setRunIdCurrentDisplaying(int runIdCurrentDisplaying) {
        this.runIdCurrentDisplaying = runIdCurrentDisplaying;
    }



    public void displayRunningSimulationResults() {
        try {
            RunResultInfo runtimeResult = engine.displayRunResultsInformation(runIdCurrentDisplaying);

            updateRunIDAndStartTimeLabels(runtimeResult);
            setEntitiesAmountTable(runtimeResult);
            //updateEntitiesGrid(runtimeResult);

            while ((Objects.equals(runtimeResult.getCurrentState(), "RUNNING") || Objects.equals(runtimeResult.getCurrentState(), "PAUSED")) && runtimeResult.getRunID() == runIdCurrentDisplaying) {
                updateSimulationRuntimeComponents(runtimeResult);

                if (Objects.equals(runtimeResult.getCurrentState(), "PAUSED") && runtimeResult.getRunID() == runIdCurrentDisplaying) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                sleep(500);//כדי לא להכביד על ההדפסות

                runtimeResult = engine.displayRunResultsInformation(runtimeResult.getRunID());
            }
            if (Objects.equals(runtimeResult.getCurrentState(), "CANCELLED")) {
                RunResultInfo finalRuntimeResult = runtimeResult;
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setContentText(finalRuntimeResult.getCancelReason());
                    errorAlert.show();
                });
                mainController.switchToDoneResultPage();
            }
            if (Objects.equals(runtimeResult.getCurrentState(), "DONE")) {

                mainController.switchToDoneResultPage();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
    }


    private void setEntitiesAmountTable(RunResultInfo runtimeResult) {
        Platform.runLater(() -> {
            currentEntitiesAmountTable.getColumns().clear();
            entityTableItemList.clear();

            if(runtimeResult.getCurrentWorldResult() != null) {

                runtimeResult.getCurrentWorldResult().getAllEntities().forEach((entName, entArr) -> {
                    entityTableItemList.add(new EntityTableItem(entName, String.valueOf(entArr.size())));
                });

                for (EntityTableItem entityData : entityTableItemList) {
                    TableColumn<ObservableList<EntityTableItem>, String> entityColumn = new TableColumn<>(entityData.getName());
                    entityColumn.setCellValueFactory(param -> new SimpleStringProperty((param.getValue().get(entityTableItemList.indexOf(entityData)).getAmount())));
                    currentEntitiesAmountTable.getColumns().add(entityColumn);
                }

                currentEntitiesAmountTable.getItems().add(entityTableItemList);
            }
        });
    }
    void updateRunIDAndStartTimeLabels(RunResultInfo runtimeResult) {
        Platform.runLater(() -> {
            startTimeLabel.setText(MillisecondsToFormattedString(runtimeResult.getStartTime()));
            runIDLabel.setText(runtimeResult.getRunID() + "   ");
        });
    }

    void updateSimulationRuntimeComponents(RunResultInfo runtimeResult) {
        Platform.runLater(() -> {
            currentTickTextField.setText(String.valueOf(runtimeResult.getTicks()));
            currentSecondTextField.setText(String.valueOf((int) (System.currentTimeMillis() / 1000L - runtimeResult.getStartTime() / 1000L)));

        });

        if(runtimeResult.getCurrentWorldResult()!=null) {
            runtimeResult.getCurrentWorldResult().getAllEntities().forEach((entName, entArr) -> {
                int numOfEntities = entArr.size();
                entityTableItemList.forEach((item) -> {
                    if (item.getName() == entName) {
                        Platform.runLater(() -> {
                            item.setAmount(String.valueOf(numOfEntities));
                            currentEntitiesAmountTable.refresh();
                        });

                    }
                });
            });
        }
    }

    // i tried so hard and got so far in the end it doesn't even matter

    /*@FXML public void handleShowGrid(ActionEvent actionEvent) {
        *//*
        showGridCheckBox.setOnAction(event -> {
            showGrid = !(showGridCheckBox.isSelected());
        });
        if (showGrid) {
            //updateEntitiesGrid(runtimeResult);
            updateGrid(runtimeResult);
        }
         *//*

        //updateGrid(currentRuntimeResult);
    }*/



    public String MillisecondsToFormattedString(Long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy | HH.mm.ss");
        Date date = new Date(time);
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public void setMainController(ResultsPageController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }
}

/*public void updateEntitiesGrid(RunResultInfo runtimeResult){
        WorldResultInfo currentWorldResult = runtimeResult.getCurrentWorldResult();
        GridResultInfo grid = currentWorldResult.getGrid();
        EntityResultInfo[][] entityMatrix = grid.getEntityMatrix();

        int currentRows = grid.getNumOfRows();
        int currentCols = grid.getNumOfCols();


        if(entitiesGrid.getChildren() != null) {
            Platform.runLater(() -> entitiesGrid.getChildren().clear());
        }

        for(int i=0 ; i < currentRows ; i++) {
            for(int j=0 ; j < currentCols ; j++) {
                Label label = new Label(":-)");
                label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

                int finalJ = j, finalI = i;

                if(entitiesGrid != null){
                    Platform.runLater(() -> entitiesGrid.add(label, finalJ, finalI));
                }
            }
        }
    }



    public void updateGrid(RunResultInfo runtimeResult){
        Map<String, ArrayList<EntityResultInfo>> allEntities = runtimeResult.getCurrentWorldResult().getAllEntities();

        if(entitiesGrid.getChildren() != null) {
            Platform.runLater(() -> entitiesGrid.getChildren().clear());
            entitiesGrid.setHgap(1);
            entitiesGrid.setVgap(1);
        }

        ///////
        GridResultInfo grid = runtimeResult.getCurrentWorldResult().getGrid();
        int currentRows = grid.getNumOfRows();
        int currentCols = grid.getNumOfCols();
        List<Coordinate> unchangedCells = new ArrayList<>();
        for(int i=0 ; i < currentRows ; i++) {
            for (int j = 0; j < currentCols; j++) {
                unchangedCells.add(new Coordinate(i, j));
            }
        }
        ////////


        for(Map.Entry<String, ArrayList<EntityResultInfo>> entry : allEntities.entrySet()){
            String entityName = entry.getKey();
            ArrayList<EntityResultInfo> entitiesInstances = entry.getValue();
            Color randomColor = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));


            for(EntityResultInfo e : entitiesInstances) {
                int row = e.getRowOnGrid();
                int col = e.getColOnGrid();
                //unchangedCells.get()

                Label newLabel = new Label(entityName);
                newLabel.setMinSize(50, 50);
                newLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
                newLabel.setTextFill(randomColor);
                Platform.runLater(() -> entitiesGrid.add(newLabel, col, row));
            }
        }

        Platform.runLater(() -> {
            entitiesGrid.setStyle("-fx-background-color: lightgray;");
            entitiesGrid.setStyle("-fx-hgap: 0; -fx-vgap: 0; -fx-grid-lines-visible: true; -fx-grid-line-color: black;");
        });


    }


    public void updateEntitiesGrid2(RunResultInfo runtimeResult){
        WorldResultInfo currentWorldResult = runtimeResult.getCurrentWorldResult();
        GridResultInfo grid = currentWorldResult.getGrid();
        EntityResultInfo[][] entityMatrix = grid.getEntityMatrix();

        int currentRows = grid.getNumOfRows();
        int currentCols = grid.getNumOfCols();

        if(entitiesGrid != null){
            System.out.println("entitiesGrid != null");
            if(entitiesGrid.getChildren() != null){
                System.out.println("entitiesGrid.getChildren() != null");
                Platform.runLater(() -> {
                    entitiesGrid.getChildren().clear();
                    entitiesGrid.setStyle("-fx-background-color: lightgray;");
                    entitiesGrid.setStyle("-fx-hgap: 10; -fx-vgap: 10; -fx-grid-lines-visible: true; -fx-grid-line-color: black;");
                });
            }
        }


        for(int i=0 ; i < currentRows ; i++) {
            for(int j=0 ; j < currentCols ; j++) {
                Label label = new Label(":-)");

                if(entitiesGrid != null){
                    entitiesGrid.add(label, j, i);
                }

            }
        }*/




//<GridPane fx:id="entitiesGrid" gridLinesVisible="true" maxHeight="100" maxWidth="100" minHeight="20.0" minWidth="20.0" BorderPane.alignment="CENTER">
        /*entitiesGrid.setMaxHeight(100.0);
        entitiesGrid.setMaxWidth(100.0);
        entitiesGrid.setMinHeight(100.0);
        entitiesGrid.setMaxWidth(100.0);
        entitiesGrid.setAlignment(Pos.CENTER);
        entitiesGrid.setHgap(0);
        entitiesGrid.setVgap(0);
        entitiesGrid.setGridLinesVisible(true);*/

        /*for(int i=0 ; i < currentRows ; i++){
            //<RowConstraints minHeight="20.0" prefHeight="50.0" vgrow="ALWAYS" />
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setPrefHeight(100.0);
            rowConstraints.setMinHeight(50.0);
            rowConstraints.setPercentHeight(100.0 / currentRows);
            Platform.runLater(() -> {
                entitiesGrid.getRowConstraints().add(rowConstraints);
            });

            for(int j=0 ; j < currentCols ; j++){
                //<ColumnConstraints hgrow="ALWAYS" minWidth="20.0" prefWidth="100.0" />
                ColumnConstraints colConstraints = new ColumnConstraints();
                colConstraints.setHgrow(Priority.ALWAYS);
                colConstraints.setPrefWidth(100.0);
                colConstraints.setMinWidth(50.0);
                colConstraints.setPercentWidth(100.0 / currentCols);
                Platform.runLater(() -> {
                    entitiesGrid.getColumnConstraints().add(colConstraints);
                });


                Label label;
                if(entityMatrix[i][j] == null){
                    label = new Label(" ");
                }
                else {
                    label = new Label(entityMatrix[i][j].getName());
                }

                int finalJ = j;
                int finalI = i;
                Platform.runLater(() -> {
                    entitiesGrid.add(label, finalJ, finalI);
                });

            }
        }
    }
    public void updateGrid2(RunResultInfo runtimeResult){
        Map<String, ArrayList<EntityResultInfo>> allEntities = runtimeResult.getCurrentWorldResult().getAllEntities();

        //entitiesGrid.getChildren().clear(); // Clear existing labels
        if(entitiesGrid != null){
            if(entitiesGrid.getChildren() != null){
                Platform.runLater(() -> {
                    entitiesGrid.getChildren().clear();
                    entitiesGrid.setStyle("-fx-background-color: lightgray;");
                    entitiesGrid.setStyle("-fx-hgap: 10; -fx-vgap: 10; -fx-grid-lines-visible: true; -fx-grid-line-color: black;");
                });
            }
        }

        for(Map.Entry<String, ArrayList<EntityResultInfo>> entry : allEntities.entrySet()){
            String entityName = entry.getKey();
            ArrayList<EntityResultInfo> entitiesInstances = entry.getValue();

            for(EntityResultInfo e : entitiesInstances) {
                int row = e.getRowOnGrid();
                int col = e.getColOnGrid();
                Label newLabel = new Label(entityName);

                Platform.runLater(() -> {
                    entitiesGrid.add(newLabel, col, row);
                });
            }
        }
    }*/
