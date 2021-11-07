package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.reseau.MainMenuController;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * La classe MainMenu est une classe purement JavaFX et permet d'afficher le Menu principal.
 */
public class MainMenu extends Stage implements Initializable {

    // On utilise des integerProperty de JavaFX afin de pouvoir leur coller des listeners (dans la classe MainApp) pour sortir du menu
    private IntegerProperty nbPlayers;
    private IntegerProperty currPlayerID;

    public MainMenu(IntegerProperty nbPlayers, IntegerProperty currPlayerID){
        super();
        this.nbPlayers = nbPlayers;
        this.currPlayerID = currPlayerID;
    }

    /**
     * On initialise la fenêtre (stage) afin de l'afficher. On appelle la classe MainMenuController qui va s'occuper des boutons et du layout
     * @param url On n'utilise pas ce paramètre dans notre application
     * @param resourceBundle Celui là non plus
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane root = new MainMenuController(nbPlayers, currPlayerID);
        this.setTitle("Menu Principal");
        this.setScene(new Scene(root, 1050, 720));
        this.show();
    }
}
