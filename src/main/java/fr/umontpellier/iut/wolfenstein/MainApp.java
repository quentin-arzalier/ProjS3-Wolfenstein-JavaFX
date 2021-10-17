package fr.umontpellier.iut.wolfenstein;

import fr.umontpellier.iut.wolfenstein.network.WolfClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainApp extends Application {


    private MainMenu mainMenu;
    private Stage primaryStage;

    private IntegerProperty nbPlayers;
    private IntegerProperty currPlayerID;


    private GameRenderer game;
    private Scene scene;
    private Player currPlayer;

    private EnemyInd garde1;

    private GridPane root;
    private Minimap minimap;

    private final boolean multiplayer = false;

    private final InvalidationListener whenPlayerAmountSet = change -> startGame();


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage){
        this.primaryStage = stage;
        this.nbPlayers = new SimpleIntegerProperty(0);
        this.currPlayerID = new SimpleIntegerProperty(0);
        stage.setTitle("Wolfenstus 3D");
        nbPlayers.addListener(whenPlayerAmountSet);
        mainMenu = new MainMenu(nbPlayers, currPlayerID);
        mainMenu.initialize(null, null);
    }

    public void startGame(){
        mainMenu.close();

        Player player1 = new Player(Color.CYAN,1);
        Player player2 = new Player(Color.RED, 2);

        if (currPlayerID.getValue() == 1){
            currPlayer = player1;
        } else {
            currPlayer = player2;
        }



        garde1 = new EnemyInd();

        root = new GridPane();
        minimap = new Minimap();
        game = new GameRenderer(currPlayer, minimap);

        minimap.addJoueur(currPlayer);


        Map myMap = new Map("levels/level0.png");
        game.setMap(myMap);
        game.addEnemy(garde1);


        if (nbPlayers.getValue() > 1){
            player1.setMultiplayer();
            player2.setMultiplayer();
            Player otherPlayer = player2;
            if (currPlayerID.getValue() == 2){
                otherPlayer = player1;
            }
            minimap.addJoueur(otherPlayer);
            myMap.addSprite(otherPlayer.getSprite());
            ArrayList<Player> players = new ArrayList<>();
            players.add(player1);
            players.add(player2);
            WolfClient.getInstance().setPlayers(players);
        }

     //   minimap.addEnemyInd(garde1);

        minimap.setMap("levels/level0.png");


        root.add(game, 0, 0, 4, 1);

        root.add(minimap, 4, 0, 3, 1);

        scene = new Scene(root);
        scene.setCursor(Cursor.NONE);
        primaryStage.setTitle("Projet Wolfenstus 3D");
        primaryStage.setScene(scene);
        primaryStage.show();
        gameHandlers();
    }

    private void changeLevel(int i){
        game.setMap(new Map("levels/level" + i + ".png"));
        minimap.setMap("levels/level" + i + ".png");
        currPlayer.resetPos();
        garde1.resetPos();
        game.addEnemy(garde1);
    }




    /**
     * Cette méthode permet de lire les inputs du joueur sur son clavier afin de faire fonctionner le mouvement de la caméra première personne.
     * L'évènement KEY_PRESSED permet de savoir quand une touche du clavier est appuyée, et active les boolean respectifs.
     * L'évènement KEY_RELEASED détecte le moment où les touches sont relachées, et désactive les boolean respectifs.
     */
    private void gameHandlers() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.UP || code == KeyCode.Z) {
                currPlayer.setUp(true);
            }
            else if (code == KeyCode.DOWN || code == KeyCode.S) {
                currPlayer.setDown(true);
            }
            else if(code == KeyCode.LEFT || code == KeyCode.Q) {
                currPlayer.setLeft(true);
            }
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                currPlayer.setRight(true);
            }
            else if (code == KeyCode.NUMPAD0) {
                changeLevel(0);
            }
            else if (code == KeyCode.NUMPAD1) {
                changeLevel(1);
            }
            else if (code == KeyCode.NUMPAD2) {
                changeLevel(2);
            }
            else if (code == KeyCode.NUMPAD3) {
                changeLevel(3);
            }
            else if (code == KeyCode.NUMPAD4) {
                changeLevel(4);
            }
            else if (code == KeyCode.NUMPAD5) {
                changeLevel(5);
            }
            else if (code == KeyCode.ESCAPE){
                Platform.exit();
            }
            key.consume();
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.UP || code == KeyCode.Z) {
                currPlayer.setUp(false);
            }
            else if (code == KeyCode.DOWN || code == KeyCode.S) {
                currPlayer.setDown(false);
            }
            else if(code == KeyCode.LEFT || code == KeyCode.Q) {
                currPlayer.setLeft(false);
            }
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                currPlayer.setRight(false);
            }
            key.consume();
        });
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, (mouse) -> {
            int milieuX = (int)Screen.getPrimary().getBounds().getWidth()/2;
            int milieuY = (int)Screen.getPrimary().getBounds().getHeight()/2;
            if (mouse.getScreenY() > milieuY+1 || mouse.getScreenY() < milieuY-1){
                currPlayer.moveCameraPitch(-(float)(mouse.getScreenY() - milieuY)*0.8f);
            }
            if (mouse.getScreenX() < milieuX - 1){
                currPlayer.lookLeft((float)(mouse.getScreenX() - milieuX)*0.1f);
            }
            else if (mouse.getScreenX() > milieuX + 1){
                currPlayer.lookRight((float)(mouse.getScreenX() - milieuX)*0.1f);
            }
            moveCursor(milieuX, milieuY);
        });
    }

    public void moveCursor(int screenX, int screenY) {
        Platform.runLater(() -> {
            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX, screenY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
