package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.graphismes.Sprite;
import javafx.scene.paint.Color;

/**
 * La classe player définit les objets situés dans
 */
public class Player {

    private Color color;
    private Sprite sprite;


    /**
     * La position du joueur dans la matrice (sur l'axe X)
     */
    private float posX = 16.5f;

    /**
     * La position du joueur dans la matrice (sur l'axe Y)
     */
    private float posY = 16.5f;

    /**
     * La coordonnée X du vecteur direction du joueur
     */
    private float vx = 0;

    /**
     * La coordonnée Y du vecteur direction du joueur
     */
    private float vy = 1;

    private int playerID;

    public Player(){
        sprite = new Sprite(posX, posY, "player" + 1);
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public int getPlayerID() {
        return playerID;
    }

    public Color getColor() {
        return color;
    }

    public Sprite getSprite() {
        return sprite;
    }


    /**
     * Les éléments ci-dessous ne sont utilisés qu'en cas de multijoueur
     */

    private boolean isMultiplayer = false;

    public void setMultiplayer(Color c, int playerID){
        this.color = c;
        this.sprite = new Sprite(posX, posY, "player" + playerID);
        isMultiplayer = true;
        this.playerID = playerID;
    }

    public boolean getMultiplayer(){
        return this.isMultiplayer;
    }

    public void setPosWithString(String posString){
        String list = posString.split(":")[1];
        String[] info = list.split(", ");
        this.posX = Float.parseFloat(info[0]);
        this.posY = Float.parseFloat(info[1]);
        this.vx = Float.parseFloat(info[2]);
        this.vy = Float.parseFloat(info[3]);
        sprite.updatePos(posX, posY);
    }
}