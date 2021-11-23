package fr.umontpellier.iut.wolfenstein.graphismes;

import javafx.scene.image.Image;

/**
 * Cette classe étend la classe Sprite, qui prend déjà en charge les éléments à dessiner qui ne sont pas des murs.
 * Elle permet d'altérer le comportement d'un sprite et de le rendre capable de mouvement ou d'affichage en plusieurs directions:
 * Ce sont les ennemis.
 */


public class Enemy extends Sprite{

    /** Définit la frame actuelle de l'animation */
    private int currFrame = 0;

    /** Définit les directions X et Y de l'ennemi*/
    private int dirX = 1;
    private int dirY = 0;

    /** Définit de manière non modifiable la vitesse de déplacement du sprite */
    private final float moveSpeed = 0.025f;

    private long lastFrameTime;
    private long lastMoveTime;

    public Enemy(float posX, float posY, String tex) {
        super(posX, posY, tex);
    }

    @Override
    public void setDist(float playerX, float playerY) {
        super.setDist(playerX, playerY);

        String tex;
        if (currFrame != 0){
            tex = "sprites/garde/" + direction(playerX, playerY) +"/"+ "garde" + currFrame + ".png";
        }
        else {
            tex = "sprites/garde/" + direction(playerX, playerY) +"/garde.png";
        }


        if (getCurrTime() - lastFrameTime >= 200_000_000){
            lastFrameTime = getCurrTime();
            nextFrame();
        }
        if (getCurrTime() - lastMoveTime >= 1_000_000_000){
            lastMoveTime = getCurrTime();
            if (currFrame == 0){
                start();
                changeDirection();
            }
            else {
                stop();
            }
        }

        if (currFrame != 0) {
            move();
        }

        this.setTex(new Image(tex));
    }

    /**
     * Permet d'obtenir le sprite de l'ennemi en fonction de sa rotation par rapport au joueur
     * @param playerX La position X du joueur
     * @param playerY La position Y du joueur
     * @return L'indice du dossier dans lequel se trouve l
     */
    public String direction(float playerX, float playerY){
        double DistY = playerY - getPosY();

        double rayonXG = getPosX() - (Math.abs(DistY)/2) ;
        double rayonXD = getPosX() + (Math.abs(DistY)/2 );

        double rayonXG2 = getPosX() - (Math.abs(DistY)*2) ;
        double rayonXD2 = getPosX() + (Math.abs(DistY)*2 );


        if (DistY  >= 0)   {
            if(playerX > rayonXD){
                if (playerX < rayonXD2){
                    return "2"; //
                }
                else {
                    return "3";
                }

            }
            else if(playerX < rayonXG) {
                if (playerX > rayonXG2){
                    return "8";
                }
                else {
                    return "7";
                }
            }
            else {
                return "1"; // Fait face au joueur
            }
        }
        else {
            if(playerX > rayonXD){
                if (playerX < rayonXD2){
                    return "4";
                }
                else {
                    return "3";
                }
            }
            else if(playerX < rayonXG) {
                if (playerX > rayonXG2){
                    return "6";
                }
                else {
                    return "7";
                }
            }
            else {
                return "5";
            }
        }
    }


    public void nextFrame(){
        if (currFrame != 0){
            currFrame++;
            if (currFrame == 5){
                currFrame = 1;
            }
        }
    }

    public void stop(){
        currFrame = 0;
    }

    public void start(){
        currFrame = 1;
    }

    private void move() {
        float newX = getPosX() + dirX*moveSpeed;
        float newY = getPosY() + dirY*moveSpeed;
        if (getWorldmap()[(int)newX][(int)newY] == 0){
            updatePos(newX,newY);
        }
    }

    private void changeDirection() {
        if (dirX == 1){
            dirX = 0;
            dirY = 1;
        }
        else if (dirX == -1) {
            dirX = 0;
            dirY = -1;
        }
        else {
            if (dirY == 1){
                dirX = -1;
                dirY = 0;
            }
            else if (dirY == -1){
                dirX = 1;
                dirY = 0;
            }
        }
    }
}
