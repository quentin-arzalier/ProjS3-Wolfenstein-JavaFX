package test.fr.umontpellier.iut.wolfenstein;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage){
        Pane root = new Pane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.close();
        Platform.exit();
    }
}
