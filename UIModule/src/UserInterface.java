import engine.Engine;
import engine.EngineInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UserInterface extends Application {
    Engine engine;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Predictions");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("example.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        PRDController controller = fxmlLoader.getController();
        controller.setModel(engine);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
