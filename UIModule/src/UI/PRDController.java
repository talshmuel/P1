package UI;

import UI.page.details.DetailsPageController;
import UI.page.execution.ExecutionPageController;
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
import javafx.scene.layout.GridPane;
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
