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

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setDist(float dist) {
        this.dist = dist;
    }

    public void setTexname(String texname) {
        this.texname = texname;
    }

    public void setTex(Image tex) {
        this.tex = tex;
    }
}
