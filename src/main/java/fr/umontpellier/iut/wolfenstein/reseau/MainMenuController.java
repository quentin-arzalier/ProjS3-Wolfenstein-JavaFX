package fr.umontpellier.iut.wolfenstein.reseau;

import fr.umontpellier.iut.wolfenstein.reseau.WolfClient;
import fr.umontpellier.iut.wolfenstein.reseau.WolfServer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Toute cette classe ne contient rien d'important dans le cadre du projet, qui avait pour objectif principal de recréer le moteur graphique de
 * wolfenstein 3D. Il n'est donc probablement pas pertinent pour la correction de celui çi de regarder ce que fait cette classe.
 * En résumé : Elle gère le menu princpal et communique à MainApp le nombre de joueurs et l'id du joueur actuel.
 */
public class MainMenuController extends GridPane {

    private IntegerProperty nbPlayers;

    private IntegerProperty currPlayerID;

    private int localNbPlayers;

    private ObservableList<String> memberListDisp;

    private boolean isHost = false;


    // Début de la déclaration des éléments du FXML
    @FXML
    private VBox connectedMenu;

    @FXML
    private VBox firstMenu;

    @FXML
    private Button playAlone;

    @FXML
    private Button startButton;

    @FXML
    private Button hostButton;

    @FXML
    private Button joinButton;

    @FXML
    private TextField ipField;

    @FXML
    private ListView<String> memberListView;

    // Fin de la déclaration des éléments du FXML

    public MainMenuController(IntegerProperty nbPlayers, IntegerProperty currPlayerID){
        this.nbPlayers = nbPlayers;
        this.currPlayerID = currPlayerID;
        this.localNbPlayers = 1;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);


        try {
            loader.load();
            connectedMenu.setVisible(false);
            startButton.setVisible(false);

            playAlone.setOnAction(event -> startSoloGame());
            hostButton.setOnAction(event -> hostGame());
            joinButton.setOnAction(event -> joinGame());
            startButton.setOnAction(event -> startGame());

            memberListDisp = FXCollections.observableArrayList();
            memberListView.setItems(memberListDisp);
            memberListView.getSelectionModel().selectFirst();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void updateList(String list){
        String[] members = list.split(", ");
        memberListDisp = FXCollections.observableArrayList(members);
        Platform.runLater(() -> memberListView.setItems(memberListDisp));
        localNbPlayers = memberListDisp.size();
    }

    @FXML
    private void startSoloGame(){
        currPlayerID.setValue(1);
        nbPlayers.setValue(1);
    }

    @FXML
    private void hostGame(){
        WolfServer.startServer();
        connectedMenu.setVisible(true);
        firstMenu.setVisible(false);
        ipField.setText("localhost");
        isHost = true;
        joinGame();
    }

    @FXML
    private void joinGame(){
        if (!ipField.getText().equals("")){
            try {
                if (isHost){
                    startButton.setVisible(true);
                }
                WolfClient.setInstance(ipField.getText(), this);
                WolfClient.getInstance().sendCommand("ADDPLAYER");
                if (WolfClient.getInstance().getPlayerNumber() >4){
                    System.out.println("Trop de joueurs dans le salon");
                }
                else {
                    connectedMenu.setVisible(true);
                    firstMenu.setVisible(false);
                }
            } catch (Exception e){
                System.out.println("Le serveur n'est pas démarré");
            }
        }
    }

    @FXML
    private void startGame(){
        WolfServer.sendToAll("STARTGAME");
    }

    public void startMultiplayerGame(int playerNumber) {
        currPlayerID.setValue(playerNumber);
        nbPlayers.setValue(localNbPlayers);
    }
}
