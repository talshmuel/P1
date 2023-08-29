package UI.page.results;

import UI.PRDController;
import engine.EngineInterface;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ResultsPageController {

    private EngineInterface engine;
    private PRDController mainController;

    @FXML
    private ListView<String> resultsListView;

    public void addItemToList(String newItem){
        resultsListView.getItems().add(newItem);
    }



    public void setMainController(PRDController mainController) {
        this.mainController = mainController;
    }
    public void setModel(EngineInterface engine) {
        this.engine = engine;
    }

}
