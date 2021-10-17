package fr.umontpellier.iut.wolfenstein;

import javafx.scene.image.Image;

import java.util.Objects;

public class Enemy extends Sprite{

    public double dir;
    public double time;
    public Enemy(float posX, float posY, String tex) {
        super(posX, posY, tex);
        dir = Math.random();
    }

    @Override
    public void setDist(float playerX, float playerY) {
        super.setDist(playerX, playerY);


        this.setTex(new Image("sprites/" +Direction(playerX,playerY) +"/"+ "garde" + ".png"));



       // Direction2(playerX,playerY);
    }

    public String Direction(float playerX, float playerY){
        double DistY = playerY - super.getPosY();

        double rayonXG = super.getPosX() - (Math.abs(DistY)/2) ;
        double rayonXD = super.getPosX() + (Math.abs(DistY)/2 );

        double rayonXG2 = super.getPosX() - (Math.abs(DistY)*2) ;
        double rayonXD2 = super.getPosX() + (Math.abs(DistY)*2 );


        if (DistY  >= 0)   {
            if(playerX > rayonXD){
                if (playerX < rayonXD2){
                    return "2";
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
                return "1";
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
    /*
    public void Direction2(float playerX, float playerY){
        double DistY = playerY - super.getPosY();

        double rayonXG = super.getPosX() - (Math.abs(DistY)/2) ;
        double rayonXD = super.getPosX() + (Math.abs(DistY)/2 );

        double rayonXG2 = super.getPosX() - (Math.abs(DistY)*2) ;
        double rayonXD2 = super.getPosX() + (Math.abs(DistY)*2 );


        if (DistY  >= 0)   {
            if(playerX > rayonXD){
                if (playerX < rayonXD2){
                    this.setTex(new Image("sprites/" + "garde3" + ".png"));
                }
                else {
                    this.setTex(new Image("sprites/" + "garde4" + ".png"));
                }

            }
            else if(playerX < rayonXG) {
                if (playerX > rayonXG2){
                    this.setTex(new Image("sprites/" + "garde8" + ".png"));
                }
                else {
                    this.setTex(new Image("sprites/" + "garde7" + ".png"));
                }
            }
            else {
                this.setTex(new Image("sprites/" + "garde" + ".png"));
            }
        }

        else {
            if(playerX > rayonXD){
                if (playerX < rayonXD2){
                    this.setTex(new Image("sprites/" + "garde5" + ".png"));
                }
                else {
                    this.setTex(new Image("sprites/" + "garde4" + ".png"));
                }
            }
            else if(playerX < rayonXG) {
                if (playerX > rayonXG2){
                    this.setTex(new Image("sprites/" + "garde6" + ".png"));
                }
                else {
                    this.setTex(new Image("sprites/" + "garde7" + ".png"));
                }
            }
            else {
                this.setTex(new Image("sprites/" + "garde2" + ".png"));
            }
        }
    }*/

    public double getDir() {
        return dir;
    }

}
