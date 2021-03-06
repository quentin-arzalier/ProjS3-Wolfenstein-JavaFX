package fr.umontpellier.iut.wolfenstein.graphismes;

import fr.umontpellier.iut.wolfenstein.gameplay.MainPlayer;
import javafx.scene.image.Image;

public class Sprite implements Comparable<Sprite>{

    private float posX;
    private float posY;
    private float dist;
    private Image tex;

    private int[][] worldmap;

    public Sprite(float posX, float posY, String tex) {
        this.posX = posX;
        this.posY = posY;
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

    public void setTex(Image tex) {
        this.tex = tex;
    }

    public int[][] getWorldmap() {
        return worldmap;
    }

    public void setWorldmap(int[][] worldmap) {
        this.worldmap = worldmap;
    }

    public void update(float deltaTime){
        MainPlayer player = MainPlayer.getInstance();
        setDist(player.getPosX(), player.getPosY());
    }
}
