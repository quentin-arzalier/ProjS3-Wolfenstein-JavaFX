package fr.umontpellier.iut.wolfenstein.graphismes;


import fr.umontpellier.iut.wolfenstein.TestApp;
import fr.umontpellier.iut.wolfenstein.gameplay.Map;
import fr.umontpellier.iut.wolfenstein.gameplay.Player;
import fr.umontpellier.iut.wolfenstein.graphismes.GameRenderer;
import fr.umontpellier.iut.wolfenstein.graphismes.Minimap;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

class GameRendererTest {

    GameRenderer moteur;

    @BeforeAll
    static void setUp(){
        TestApp.main(new String[0]);
    }

    @BeforeEach
    void setMapUp(){
        // La carte est une carte carrée de taille 5x5 avec un contour rouge (mur type 1)
        moteur = GameRenderer.getInstance();
        moteur.setMap(new Map("test/contours.png"));
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /////                         TESTS POUR LA METHODE getDist()                   /////
    /////////////////////////////////////////////////////////////////////////////////////
    @Test
    void caseDistanceUnDemiQuandJoueurDistanceUnDemiRegardeDroit(){
        // Le joueur est placé au milieu de la salle et regarde droit
        double dist = moteur.getDist(1, 2.5);
        assertEquals(0.5,dist);
    }

    @Test
    void caseDistanceUnQuandJoueurDistanceZeroRegardeDroit(){
        double dist = moteur.getDist(-1, 2);
        assertEquals(1,dist);
        dist = moteur.getDist(1, 2);
        assertEquals(1,dist);
    }

    @Test
    void caseDistanceDemiRacineDeDeuxQuandJoueurDistanceUnDemiRegardeDiagonale(){
        // Le joueur est placé au milieu de la salle et regarde en diagonale
        float rayDir = (float)Math.sqrt(0.5); // Pour que le joueur regarde en diagonale
        float dist = (float)moteur.getDist(rayDir, 2.5);
        assertEquals((float)Math.sqrt(2)/2,dist);
    }

    @Test
    void caseDistanceRacineDeDeuxQuandJoueurDistance0RegardeDiagonale(){
        // Le joueur est placé au milieu de la salle et regarde en diagonale
        float rayDir = (float)Math.sqrt(0.5); // Pour que le joueur regarde en diagonale
        float dist = (float)moteur.getDist(rayDir, 2);
        assertEquals((float)Math.sqrt(2),dist);
        dist = (float)moteur.getDist(-rayDir, 2);
        assertEquals((float)Math.sqrt(2),dist);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /////                         TESTS POUR LA METHODE startDDA()                  /////
    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    void infosDDACorrectesJoueurMilieuSalleRegardeX(){
        DDAInfo ddaInfo = moteur.startDDA(1, 0, 2.5f, 2.5f);
        assertEquals(1.5, ddaInfo.getWallDist());
        assertEquals(1,   ddaInfo.getHit());
        assertEquals(4.0, ddaInfo.getNewPosX());
        assertEquals(2.5, ddaInfo.getNewPosY());
        assertEquals(0,   ddaInfo.getSide());
    }

    @Test
    void infosDDACorrectesJoueurMilieuSalleRegardeY(){
        DDAInfo ddaInfo = moteur.startDDA(0, 1, 2.5f, 2.5f);
        assertEquals(1.5, ddaInfo.getWallDist());
        assertEquals(1,   ddaInfo.getHit());
        assertEquals(2.5, ddaInfo.getNewPosX());
        assertEquals(4.0, ddaInfo.getNewPosY());
        assertEquals(1,   ddaInfo.getSide());
    }

    @Test
    void chooseColorRetourneBonneCouleur(){
        Color test = moteur.chooseColor(5, 0, 0, 0);
        assertEquals(test, Color.web("282828"));
    }



}