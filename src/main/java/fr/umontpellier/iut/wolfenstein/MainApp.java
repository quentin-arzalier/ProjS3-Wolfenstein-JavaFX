package fr.umontpellier.iut.wolfenstein;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private GameRenderer game;
    private Scene scene;
    private Player currPlayer;
    private GridPane root;
    private Minimap minimap;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){

        currPlayer = new Player();
        root = new GridPane();
        minimap = new Minimap();
        game = new GameRenderer(currPlayer, minimap);


        minimap.setMap("levels/level3.png");
        game.setMap(new Map("levels/level3.png"));


        root.add(game, 0, 0, 4, 1);
        root.add(minimap, 3, 0, 3, 1);

        addButtons();

        scene = new Scene(root);
        primaryStage.setTitle("Projet Wolfenstus 3D");
        primaryStage.setScene(scene);
        primaryStage.show();
        gameHandlers();
    }

    private void addButtons(){
        Button button;
        for (int i = 0; i < 6; i++) {
            button = new Button("Level " + i);
            int finalI = i;
            button.setOnMouseClicked((actionEvent -> changeLevel(finalI)));
            if (i < 3){
                button.setMinWidth(960/3f);
            }
            else {
                button.setMinWidth(200f);
            }
            button.setMinHeight(100f);
            button.setFocusTraversable(false);
            root.add(button, i, 1, 1, 1);
            
        }
    }

    private void changeLevel(int i){
        game.setMap(new Map("levels/level" + i + ".png"));
        minimap.setMap("levels/level" + i + ".png");
        currPlayer.resetPos();
    }




    /**
     * Cette méthode permet de lire les inputs du joueur sur son clavier afin de faire fonctionner le mouvement de la caméra première personne.
     * L'évènement KEY_PRESSED permet de savoir quand une touche du clavier est appuyée, et active les boolean respectifs.
     * L'évènement KEY_RELEASED détecte le moment où les touches sont relachées, et désactive les boolean respectifs.
     */
    private void gameHandlers() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            if(code == KeyCode.LEFT || code == KeyCode.Q) {
                currPlayer.setLeft(true);
            }
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                currPlayer.setRight(true);
            }
            else if (code == KeyCode.UP || code == KeyCode.Z) {
                currPlayer.setUp(true);
            }
            else if (code == KeyCode.DOWN || code == KeyCode.S) {
                currPlayer.setDown(true);
            }
            else if (code == KeyCode.ESCAPE){
                Platform.exit();
            }
            key.consume();
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.Q) {
                currPlayer.setLeft(false);
            }
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                currPlayer.setRight(false);
            }
            else if (code == KeyCode.UP || code == KeyCode.Z) {
                currPlayer.setUp(false);
            }
            else if (code == KeyCode.DOWN || code == KeyCode.S) {
                currPlayer.setDown(false);
            }
            key.consume();
        });
    }
}
