package fr.umontpellier.iut.wolfenstein;

import javafx.animation.AnimationTimer;
import javafx.scene.image.WritableImage;

public class Player {

    // La position du joueur dans le quadrillage
    private float posX = 16.5f;
    private float posY = 16.5f;

    // Le vecteur direction du joueur
    private float vx = -1;
    private float vy = 0;

    // Le vecteur direction de la caméra (perpendiculaire au joueur)
    private float latX = 0;
    private float latY = 1;

    // Les vitesses de déplacement du joueur
    private float moveSpeed = 0;
    private float rotSpeed = 0;

    // Les valeurs booléennes qui servent pour déplacer le joueur
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;


    public Player(){}

    public void resetPos() {
        posX = 16.5f;
        posY = 16.5f;
        vx = -1;
        vy = 0;
        latX = 0;
        latY = 1;
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
        if(isRight) {
            float oldVx = vx;
            vx = (float) (vx * Math.cos(-rotSpeed) - vy * Math.sin(-rotSpeed));
            vy = (float) (oldVx * Math.sin(-rotSpeed) + vy * Math.cos(-rotSpeed));
            float oldLatx = latX;
            latX = (float) (latX * Math.cos(-rotSpeed) - latY * Math.sin(-rotSpeed));
            latY = (float) (oldLatx * Math.sin(-rotSpeed) + latY * Math.cos(-rotSpeed));
        }
        if (isLeft) {
            float oldVx = vx;
            vx = (float) (vx * Math.cos(rotSpeed) - vy * Math.sin(rotSpeed));
            vy = (float) (oldVx * Math.sin(rotSpeed) + vy * Math.cos(rotSpeed));
            float oldLatx = latX;
            latX = (float) (latX * Math.cos(rotSpeed) - latY * Math.sin(rotSpeed));
            latY = (float) (oldLatx * Math.sin(rotSpeed) + latY * Math.cos(rotSpeed));
        }
        if (isUp && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
            if (worldMap[(int)(posX + vx * moveSpeed)][(int)posY] == 0) posX += vx * moveSpeed;
            if (worldMap[(int)posX][(int)(posY + vy * moveSpeed)] == 0) posY += vy * moveSpeed;
        }
        if (isDown && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
            if (worldMap[(int)(posX - vx * moveSpeed)][(int)posY] == 0) posX -= vx * moveSpeed;
            if (worldMap[(int)posX][(int)(posY - vy * moveSpeed)] == 0) posY -= vy * moveSpeed;
        }
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

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setRotSpeed(float rotSpeed) {
        this.rotSpeed = rotSpeed;
    }
}
