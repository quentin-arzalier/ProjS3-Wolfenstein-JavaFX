package fr.umontpellier.iut.wolfenstein;

import javafx.scene.image.Image;

import java.util.Objects;

public class Sprite implements Comparable<Sprite>{
    private float posX;
    private float posY;
    private float dist;
    private String texname;
    private Image tex;

    public Sprite(float posX, float posY, String tex) {
        this.posX = posX;
        this.posY = posY;
        this.texname = tex;
        this.tex = new Image("sprites/" + tex + ".png");
    }

    @Override
    public String toString() {
        return "Sprite{" +
                "posX=" + posX +
                ", posY=" + posY +
                ", tex=" + tex.getUrl() +
                ", dist=" + dist +
                '}';
    }

    public void setDist(float playerX, float playerY){
        dist = (playerX - posX) * (playerX - posX) + (playerY - posY) * (playerY - posY);
        Enemie(playerX,playerY);

    }

    public void Enemie(float playerX, float playerY){
        if(Objects.equals(texname, "garde")) {
            if (playerY - posY > 0) {
                this.tex = new Image("sprites/" + "garde2" + ".png");
            } else {
                this.tex = new Image("sprites/" + "garde" + ".png");
            }
        }
    }
    @Override
    public int compareTo(Sprite o) {
        int retour = -1;
        if (o.dist > dist){
            retour = 1;
        }
        else if (o.dist == dist){
            retour = 0;
        }
        return retour;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void updatePos(float posX, float posY){
        this.posX = posX;
        this.posY = posY;
    }

    public Image getTex() {
        return tex;
    }
}
