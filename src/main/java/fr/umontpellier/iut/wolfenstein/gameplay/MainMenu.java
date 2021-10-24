package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.reseau.MainMenuController;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenu extends Stage implements Initializable {

    private IntegerProperty nbPlayers;
    private IntegerProperty currPlayerID;

    public MainMenu(IntegerProperty nbPlayers, IntegerProperty currPlayerID){
        super();
        this.nbPlayers = nbPlayers;
        this.currPlayerID = currPlayerID;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane root = new MainMenuController(nbPlayers, currPlayerID);
        this.setTitle("Menu Principal");
        this.setScene(new Scene(root, 1050, 720));
        this.show();
    }
}
