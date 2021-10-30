package fr.umontpellier.iut.wolfenstein;

import fr.umontpellier.iut.wolfenstein.gameplay.MainMenu;
import fr.umontpellier.iut.wolfenstein.gameplay.Map;
import fr.umontpellier.iut.wolfenstein.gameplay.Player;
import fr.umontpellier.iut.wolfenstein.graphismes.GameRenderer;
import fr.umontpellier.iut.wolfenstein.graphismes.Minimap;
import fr.umontpellier.iut.wolfenstein.reseau.WolfClient;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Cette classe est le composant princpal du programme JavaFX, et sera composée des différentes parties graphiques du jeu.
 * Elle définit les éléments principaux de gameplay et instancie les éléments de graphismes.
 */
public class MainApp extends Application {


    private MainMenu mainMenu;
    private Stage primaryStage;

    private IntegerProperty nbPlayers;
    private IntegerProperty currPlayerID;


    private GameRenderer game;
    private Scene scene;
    private Player currPlayer;
    private Player[] players;

    private Minimap minimap;

    private final InvalidationListener whenPlayerAmountSet = change -> startGame();



    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Méthode lancée dès le démarrage de l'application (par l'appel de launch() dans main)
     * @param stage La fenêtre de l'application
     */
    @Override
    public void start(Stage stage){
        this.primaryStage = stage;
        this.nbPlayers = new SimpleIntegerProperty(0);
        this.currPlayerID = new SimpleIntegerProperty(0);
        stage.setTitle("Wolfenstus 3D");

        // Les trois lignes suivantes permettent de démarrer le menu principal et de faire fonctionner le lancement du jeu
        nbPlayers.addListener(whenPlayerAmountSet);
        mainMenu = new MainMenu(nbPlayers, currPlayerID);
        mainMenu.initialize(null, null);
    }

    /**
     * Une fois que le nombre de joueur a été défini par le menu princpal (1 si jouer solo ou lancement seul après hébergement, plus sinon),
     * cette méthode instancie les divers éléments de gameplay servant à faire l'affichage du jeu pour chaque point de vue.
     */
    public void startGame(){

        // On ferme le menu principal
        mainMenu.close();

        // On instancie les joueurs de la partie et on choisit le joueur de l'application courante
        createPlayers();

        // On instancie la minimap et on lui donne ce qu'il faut
        minimapInit();

        // on instancie le moteur de jeu
        game = new GameRenderer(currPlayer, minimap);
        game.setMap(createMap(0));


        setupWindow();

        // Finalement, on déclare le système de gestion d'entrées utilisateur
        gameHandlers();
    }

    /**
     * Cette méthode est responsable de la création du layout de l'application.
     */
    private void setupWindow() {
        // On colle horizontalement le moteur de jeu et la minimap sur la fenêtre de l'application
        HBox root = new HBox();
        root.getChildren().add(game);
        root.getChildren().add(minimap);

        // On instancie les différents éléments d'UI utilisés dans l'application
        scene = new Scene(root);
        scene.setCursor(Cursor.NONE);
        primaryStage.setTitle("Wolfenstus 3D");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Cette méthode va créer la carte de jeu en créant la matrice des murs et en plaçant les divers sprites
     * @return Un objet de type Map contenant toutes les informations sur le niveau actuel
     * @param i L'identifiant du niveau sélectionné
     */
    private Map createMap(int i) {
        // On instancie la carte avec une image qui sera analysée dans son constructeur
        Map myMap = new Map("levels/level" + i + ".png");

        // On ajoute les sprites des différents joueurs à la carte afin qu'ils puissent se voir entre eux
        if (nbPlayers.getValue() > 1){
            for(Player player : players){
                myMap.addSprite(player.getSprite());
            }
        }
        return myMap;
    }

    /**
     * Cette méthode crée la minimap affichée à droite de l'écran et lui donne les différents joueurs à afficher
     */
    private void minimapInit() {
        minimap = new Minimap();
        // Permet d'afficher chaque joueur sur la minimap
        for(Player player : players){
            minimap.addJoueur(player);
        }
        minimap.setMap("levels/level0.png");
    }

    /**
     * Cette méthode va remplir la liste de joueurs en fonction du nombre de joueurs entrés dans le menu principal.
     */
    private void createPlayers(){
        players = new Player[nbPlayers.get()];

        Player player1 = new Player(Color.CYAN,1);
        players[0] = player1;
        if (currPlayerID.get() == 1){
            currPlayer = player1;
        }

        if (nbPlayers.get() >= 2){
            Player player2 = new Player(Color.RED, 2);
            players[1] = player2;
            if (currPlayerID.get() == 2){
                currPlayer = player2;
            }
        }

        if (nbPlayers.get() >= 3){
            Player player3 = new Player(Color.YELLOW, 3);
            players[2] = player3;
            if (currPlayerID.get() == 3){
                currPlayer = player3;
            }
        }

        if (nbPlayers.get() == 4){
            Player player4 = new Player(Color.GREEN, 4);
            players[3] = player4;
            if (currPlayerID.get() == 4){
                currPlayer = player4;
            }
        }

        if (nbPlayers.get() >= 2){
            for (Player player : players){
                player.setMultiplayer();
            }
            WolfClient.getInstance().setPlayers(new ArrayList<>(Arrays.asList(players)));
        }
    }



    /**
     * Cette méthode permet de naviguer entre les différents niveaux que nous avons dessinés jusqu'à présent.
     * @param i L'indice de sélection du niveau.
     */
    private void changeLevel(int i){
        game.setMap(createMap(i));
        minimap.setMap("levels/level" + i + ".png");
        currPlayer.resetPos(); // On replace le joueur de l'application (Le multijoueur ne fonctionne pas encore pour le
    }

    /**
     * Cette méthode permet de lire les inputs du joueur sur son clavier afin de faire fonctionner le mouvement de la caméra première personne.
     * L'évènement KEY_PRESSED permet de savoir quand une touche du clavier est appuyée, et active les boolean respectifs.
     * L'évènement KEY_RELEASED détecte le moment où les touches sont relachées, et désactive les boolean respectifs.
     * L'évènement MOUSE_MOVED permet de savoir quand est-ce-que le joueur bouge la souris, et on déplace la caméra en fonction du mouvement
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
            moveCursor();
        });
    }

    /**
     * Cette méthode déplace le curseur du joueur au centre de l'écran afin de pouvoir capter proprepement les mouvements
     * de souris du joueur et de déplacer la caméra en conséquence.
     */
    public void moveCursor() {
        Platform.runLater(() -> {
            try {
                Robot robot = new Robot();
                robot.mouseMove(Screen.getPrimary().getBounds().getWidth()/2, Screen.getPrimary().getBounds().getHeight()/2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
