package UI;

import UI.page.details.DetailsPageController;
import UI.page.execution.ExecutionPageController;
import data.transfer.object.DataFromUser;
import engine.Engine;
import engine.EngineInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class PRDController {
    @FXML private SplitPane detailsPageComponent;
    @FXML private DetailsPageController detailsPageComponentController;
    @FXML private GridPane executionPageComponent;
    @FXML private ExecutionPageController executionPageComponentController;

//    @FXML private SplitPane resultsPageComponent;//זה כנראה לא יהיה ספליט פיין צריך לעדכן!!!
//    @FXML private DetailsPageController resultsPageComponentController;

    private EngineInterface engine;

    @FXML
    private TextField filePathField;

    @FXML
    private Button loadXMLButton;

    @FXML
    private Button queueManagementBotton;

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
            DataFromUser detailsToRun  = new DataFromUser();
            detailsPageComponentController.setWorldDetailsTree();
            executionPageComponentController.setEnvironmentTable();
            executionPageComponentController.setEntityTable();
        }
    }

    @FXML
    void showWorldTree(ActionEvent event) {


    }

    public void setModel(EngineInterface engine) {
        detailsPageComponentController.setModel(engine);
        executionPageComponentController.setModel(engine);
        //resultsPageComponentController.setModel(engine);
    }

    @FXML
    public void initialize() {
        if (detailsPageComponentController != null) {
            detailsPageComponentController.setMainController(this);
            executionPageComponentController.setMainController(this);
            //resultsPageComponentController.setMainController(this);
        }
    }

    public void setDetailsPageController(DetailsPageController detailsPageController) {
        this.detailsPageComponentController = detailsPageController;
        detailsPageController.setMainController(this);
    }

}
