package fr.umontpellier.iut.wolfenstein.graphismes;

import fr.umontpellier.iut.wolfenstein.gameplay.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * La classe Minimap correspond à l'affichage de la carte sur la partie droite de l'écran.
 * Il s'agit de code JavaFX et donc il n'y a pas de logique complexe.
 */
public class Minimap extends StackPane {

    private static Minimap instance;

    private final GraphicsContext contextBG;
    private final GraphicsContext contextPlayer;
    private final Text fpsCounter;
    private Image currImage;
    private float currScaleX;
    private float currScaleY;
    private final ArrayList<Player> joueurs;

    private float lastUpdate;
    private int frames;



    private Minimap(){
        joueurs = new ArrayList<>();
        this.setMaxWidth(600);
        this.setMaxHeight(600);
        this.lastUpdate = 0;
        this.frames = 0;

        // Le canevas sur lequel sera dessiné la carte
        Canvas background = new Canvas(600, 600);
        contextBG = background.getGraphicsContext2D();
        contextBG.setImageSmoothing(false);

        // Le canevas sur lequel seront dessinés le ou les joueurs.
        Canvas playerPane = new Canvas(600, 600);
        currScaleX = 1;
        currScaleY = 1;
        contextPlayer = playerPane.getGraphicsContext2D();

        // Le compteur d'images situé en haut à gauche de l'écran
        Font fpsFont = new Font("Verdana", 20);
        fpsCounter = new Text("FPS : XX");
        fpsCounter.setStroke(Color.YELLOW);
        fpsCounter.setFill(Color.YELLOW);
        fpsCounter.setFont(fpsFont);
        StackPane.setAlignment(fpsCounter, Pos.TOP_RIGHT);
        StackPane.setMargin(fpsCounter, new Insets(5));

        this.getChildren().add(background);
        this.getChildren().add(playerPane);
        this.getChildren().add(fpsCounter);
    }

    public static Minimap getInstance() {
        if (instance == null){
            instance = new Minimap();
            instance.setMap("levels/level0.png");
        }
        return instance;
    }

    /**
     * Permet de changer l'image de fond de la carte
     * @param s L'adresse à laquelle se trouve la nouvelle image à dessiner
     */
    public void setMap(String s){
        contextBG.scale(1/currScaleX, 1/currScaleY); // Puisque tous les niveaux ne sont pas de la même taille, on doit mettre à l'échelle le canevas (context)
        contextBG.clearRect(0, 0, 600, 600);
        currImage = new Image(s);
        currScaleX = (float) (600f/currImage.getWidth());
        currScaleY = (float) (600f/currImage.getHeight());
        contextBG.scale(currScaleX, currScaleY);
        contextBG.drawImage(currImage, 0, 0);
    }

    public void addJoueur(Player p){
        joueurs.add(p);
    }

    /**
     * Cette méthode est appelée par le GameRenderer et permet de mettre à jour l'affichage de la carte.
     * @param deltaTime Le temps passé depuis la dernière actualisation (en secondes)
     */
    public void update(float deltaTime) {
        lastUpdate += deltaTime;
        contextPlayer.clearRect(0, 0, 600, 600); // On vide le canevas avant de le remplir à nouveau
        if (lastUpdate >= 1){
            lastUpdate -= 1;
            fpsCounter.setText(Integer.toString(frames));  // On actualise le texte affichant les images par secondes
            frames = 0;
        }
        contextBG.drawImage(currImage, 0, 0);    // On dessine le niveau actuel en fond de carte

        // On itère sur chaque joueur présent dans la partie de jeu
        for (Player p : joueurs){
            float posX = p.getPosX();
            float posY = p.getPosY();
            float vx = p.getVx();
            float vy = p.getVy();

            // On calcule la position du joueur à l'échelle du canevas
            int pixelPosX = (int) (posX * 600 / currImage.getWidth());
            int pixelPosY = (int) (posY * 600 / currImage.getHeight());

            // Ces angles sont utilisés pour représenter le champ de vision des joueurs sur la carte
            double angle1X = (posX + vx*3 + vy*1.5) * 600 / currImage.getWidth();
            double angle1Y = (posY + vy*3 - vx*1.5) * 600 / currImage.getHeight();
            double angle2X = (posX + vx*3 - vy*1.5) * 600 / currImage.getWidth();
            double angle2Y = (posY + vy*3 + vx*1.5) * 600 / currImage.getHeight();

            // On dessine le champ de vision du joueur en jaune transparent sur la carte
            contextPlayer.setFill(new Color(0.8, 0.8, 0.0, 0.6));
            contextPlayer.strokePolygon(new double[]{pixelPosX, angle1X, angle2X}, new double[]{pixelPosY, angle1Y, angle2Y}, 3); // On trace le triangle à dessiner
            contextPlayer.fillPolygon(new double[]{pixelPosX, angle1X, angle2X}, new double[]{pixelPosY, angle1Y, angle2Y}, 3);   // Puis on le remplit de couleur

            // On dessine un cercle centré sur la position x y du joueur
            contextPlayer.setFill(p.getColor());
            contextPlayer.strokeOval(pixelPosX-3, pixelPosY-3, 6, 6);
            contextPlayer.fillOval(pixelPosX-3, pixelPosY-3, 6, 6);
        }
        frames++;
    }
}