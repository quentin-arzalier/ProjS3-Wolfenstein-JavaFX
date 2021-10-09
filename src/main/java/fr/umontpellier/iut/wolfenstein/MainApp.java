package fr.umontpellier.iut.wolfenstein;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    private GameRenderer game1;
    private GameRenderer game2;
    private Scene scene;
    private Player player1;
    private Player player2;
    private GridPane root;
    private Minimap minimap;

    private final boolean multiplayer = false;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){

        player1 = new Player(Color.CYAN,1 );
        player2 = new Player(Color.RED, 2);
        root = new GridPane();
        minimap = new Minimap();
        game1 = new GameRenderer(player1, minimap);


        minimap.addJoueur(player1);

        minimap.setMap("levels/level0.png");

        Map p1Map = new Map("levels/level0.png");
        game1.setMap(p1Map);

        if (multiplayer){
            game2 = new GameRenderer(player2, minimap);
            minimap.addJoueur(player2);
            Map p2Map = new Map("levels/level0.png");
            p2Map.addSprite(player1.getSprite());
            p1Map.addSprite(player2.getSprite());
            game2.setMap(p2Map);
            VBox test = new VBox();
            test.getChildren().add(game1);
            test.getChildren().add(game2);
            root.add(test, 0, 0, 4, 1);
        }
        else {
            root.add(game1, 0, 0, 4, 1);
        }

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
        game1.setMap(new Map("levels/level" + i + ".png"));
        minimap.setMap("levels/level" + i + ".png");
        player1.resetPos();
        if (multiplayer){
            game2.setMap(new Map("levels/level" + i + ".png"));
            player2.resetPos();
        }
    }




    /**
     * Cette méthode permet de lire les inputs du joueur sur son clavier afin de faire fonctionner le mouvement de la caméra première personne.
     * L'évènement KEY_PRESSED permet de savoir quand une touche du clavier est appuyée, et active les boolean respectifs.
     * L'évènement KEY_RELEASED détecte le moment où les touches sont relachées, et désactive les boolean respectifs.
     */
    private void gameHandlers() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            Player currPlayer;
            if ((code == KeyCode.LEFT ||code == KeyCode.RIGHT ||code == KeyCode.UP ||code == KeyCode.DOWN) && multiplayer) {
                currPlayer = player2;
            }
            else {
                currPlayer = player1;
            }
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
            Player currPlayer;
            if ((code == KeyCode.LEFT ||code == KeyCode.RIGHT ||code == KeyCode.UP ||code == KeyCode.DOWN) && multiplayer) {
                currPlayer = player2;
            }
            else {
                currPlayer = player1;
            }
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
