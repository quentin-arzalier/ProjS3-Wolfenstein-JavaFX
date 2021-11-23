package fr.umontpellier.iut.wolfenstein.graphismes;

import fr.umontpellier.iut.wolfenstein.gameplay.Player;
import javafx.scene.image.Image;

public class Sprite implements Comparable<Sprite>{

    private float posX;
    private float posY;
    private float dist;
    private Image tex;
    // Position dans le repère local du joueur, selon l'axe de vision du joueur
    private float localX;
    // Position dans le repère local du joueur, selon l'axe orthogonal (vers la droite du joueur)
    private float localY;

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

    public void update(float deltaTime, Player p) {
        float px = p.getPosX();
        float py = p.getPosY();
        float vx = p.getVx();
        float vy = p.getVy();

        localX = (posX - px) * vx + (posY - py) * vy;
        localY = -(posX - px) * vy + (posY - py) * vx;
        dist = (px - posX) * (px - posX) + (py - posY) * (py - posY);
    }

    @Override
    public int compareTo(Sprite o) {
        return -Float.compare(localX, o.localX);
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPos(float posX, float posY){
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

    public float getLocalX() {
        return localX;
    }

    public float getLocalY() {
        return localY;
    }
}
