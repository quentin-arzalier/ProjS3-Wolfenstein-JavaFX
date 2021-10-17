package fr.umontpellier.iut.wolfenstein;

import fr.umontpellier.iut.wolfenstein.network.WolfClient;
import fr.umontpellier.iut.wolfenstein.network.WolfServer;
import javafx.animation.AnimationTimer;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Player {

    // Les informations du joueur pour le multijoueur
    private final Color color;
    private final Sprite sprite;

    // La position du joueur dans le quadrillage
    private float posX = 16.5f;
    private float posY = 16.5f;

    // Le vecteur direction du joueur
    private float vx = 0;
    private float vy = 1;

    // Le vecteur direction de la caméra (perpendiculaire au joueur)
    private float latX = -1;
    private float latY = 0;

    // La rotation de la caméra vers le haut/le bas
    private float camPitch = 0;

    // Les vitesses de déplacement du joueur
    private float moveSpeed = 0;
    private float rotSpeed = 0;

    // Les valeurs booléennes qui servent pour déplacer le joueur
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;


    private final int playerID;

    private boolean isMultiplayer = false;


    public Player(Color c, int nb){
        color = c;
        sprite = new Sprite(posX, posY, "player" + nb);
        playerID = nb;
    }

    public void setMultiplayer(){
        isMultiplayer = true;
    }

    public void resetPos() {
        posX = 16.5f;
        posY = 16.5f;
        vx = 0;
        vy = 1;
        latX = -1;
        latY = 0;
        isUp = false;
        isDown = false;
        isLeft = false;
        isRight = false;
    }

    /**
     * Cette méthode est appelée à chaque frame pour faire bouger le joueur selon les boolean de déplacement activés ou non par les touches du clavier.
     * On vérifie les états des boolean, et on tourne la caméra/ déplace le joueur en fonction de leur valeurs.
     */
    public void moveCharacter(int[][] worldMap){
        if (isUp) {
            if (worldMap[(int)(posX + vx * moveSpeed)][(int)posY] == 0) posX += vx * moveSpeed;
            if (worldMap[(int)posX][(int)(posY + vy * moveSpeed)] == 0) posY += vy * moveSpeed;
            sprite.updatePos(posX, posY);
        }
        if (isDown) {
            if (worldMap[(int)(posX - vx * moveSpeed)][(int)posY] == 0) posX -= vx * moveSpeed;
            if (worldMap[(int)posX][(int)(posY - vy * moveSpeed)] == 0) posY -= vy * moveSpeed;
            sprite.updatePos(posX, posY);
        }
        if (isRight){
            if (worldMap[(int)(posX + latX * moveSpeed)][(int)posY] == 0) posX += latX * moveSpeed;
            if (worldMap[(int)posX][(int)(posY + latY * moveSpeed)] == 0) posY += latY * moveSpeed;
            sprite.updatePos(posX, posY);
        }
        if (isLeft) {
            if (worldMap[(int)(posX - latX * moveSpeed)][(int)posY] == 0) posX -= latX * moveSpeed;
            if (worldMap[(int)posX][(int)(posY - latY * moveSpeed)] == 0) posY -= latY * moveSpeed;
            sprite.updatePos(posX, posY);
        }
        if (isMultiplayer) WolfClient.getInstance().sendCommand(getPosAsString());
    }

    public void setUp(boolean up) {
        isUp = up;
    }

    public void setDown(boolean down) {
        isDown = down;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public void moveCameraPitch(float offset) {
        this.camPitch += offset;
        if (camPitch < -200) camPitch = -200;
        if (camPitch > 200) camPitch = 200;
    }

    public void lookLeft(float input){
        float oldRotSpeed =  rotSpeed;
        rotSpeed = rotSpeed * Math.abs(input);
        float oldVx = vx;
        vx = (float) (vx * Math.cos(-rotSpeed) - vy * Math.sin(-rotSpeed));
        vy = (float) (oldVx * Math.sin(-rotSpeed) + vy * Math.cos(-rotSpeed));
        float oldLatx = latX;
        latX = (float) (latX * Math.cos(-rotSpeed) - latY * Math.sin(-rotSpeed));
        latY = (float) (oldLatx * Math.sin(-rotSpeed) + latY * Math.cos(-rotSpeed));
        rotSpeed = oldRotSpeed;

        if (isMultiplayer) WolfClient.getInstance().sendCommand(getPosAsString());
    }

    public void lookRight(float input){
        float oldRotSpeed =  rotSpeed;
        rotSpeed = rotSpeed * Math.abs(input);
        float oldVx = vx;
        vx = (float) (vx * Math.cos(rotSpeed) - vy * Math.sin(rotSpeed));
        vy = (float) (oldVx * Math.sin(rotSpeed) + vy * Math.cos(rotSpeed));
        float oldLatx = latX;
        latX = (float) (latX * Math.cos(rotSpeed) - latY * Math.sin(rotSpeed));
        latY = (float) (oldLatx * Math.sin(rotSpeed) + latY * Math.cos(rotSpeed));
        rotSpeed = oldRotSpeed;

        if (isMultiplayer) WolfClient.getInstance().sendCommand(getPosAsString());
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

    public float getLatX() {
        return latX;
    }

    public float getLatY() {
        return latY;
    }

    public float getCamPitch(){
        return camPitch;
    }

    public Color getColor() {
        return color;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setRotSpeed(float rotSpeed) {
        this.rotSpeed = rotSpeed;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public String getPosAsString(){
        return "PLAYERPOS" + playerID + ":" + posX + ", " + posY + ", " + vx + ", " + vy + ", " + latX + ", " + latY;
    }

    public void setPosWithString(String posString){
        String list = posString.split(":")[1];
        String[] info = list.split(", ");
        this.posX = Float.parseFloat(info[0]);
        this.posY = Float.parseFloat(info[1]);
        this.vx = Float.parseFloat(info[2]);
        this.vy = Float.parseFloat(info[3]);
        this.latX = Float.parseFloat(info[4]);
        this.latY = Float.parseFloat(info[5]);
        sprite.updatePos(posX, posY);
    }
}
