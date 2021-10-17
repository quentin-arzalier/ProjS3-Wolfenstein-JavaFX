package fr.umontpellier.iut.wolfenstein;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class TempEnemy extends Sprite {

    private float posX;
    private float posY;
    private float dist;
    private String texname;
    private List<List<Image>> texbase = new ArrayList<>();

    private Image tex;  private int sens = 1; private int xMult = 1;

    public TempEnemy(float posX, float posY, String tex) {
        super(posX, posY, "player1");
        this.posX = posX; this.posY = posY;
        for(int j = 0; j<8; j++) { texbase.add(new ArrayList<>()); }
        for(int i = 0; i<4; i++){
            this.texbase.get(0).add(new Image("sprites/enemy/" + tex + "/frame"+(i+1)+".png"));
        }
        this.tex = texbase.get(0).get(0);
        this.setTex(this.tex);
    }

    public void nextFrame(){
        int a = this.texbase.get(0).indexOf(this.tex);
        if(a+1==4&&sens==1 || a-1==-1&&sens==-1) {
            sens=-sens;
        }
        this.tex = this.texbase.get(0).get(a+sens);
        this.setTex(this.tex);
    }

    public void update(int[][] worldmap){
        if(collided(worldmap)) xMult=-xMult;
        float newX = posX+(xMult*(0.3f));
        posX = newX;
        updatePos(newX,posY);
        nextFrame();
    }

    public boolean collided(int[][] worldmap){
        return (worldmap[(int)(posX)][(int)posY] != 0);
    }

    @Override
    public void setDist(float playerX, float playerY) {
        super.setDist(playerX, playerY);
    }

    @Override
    public void updatePos(float posX, float posY) {
        super.updatePos(posX, posY);
    }

    @Override
    public Image getTex() {
        return super.getTex();
    }
}
