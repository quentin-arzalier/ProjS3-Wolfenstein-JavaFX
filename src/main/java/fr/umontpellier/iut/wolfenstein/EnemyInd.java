package fr.umontpellier.iut.wolfenstein;

import javafx.scene.paint.Color;

public class EnemyInd {
    private float vx = 0;
    private float vy = 1;

    private float posX = 16.5f;
    private float posY = 16.5f;

    private float moveSpeed = 0.02f;
    private float rotSpeed = 0;


    private final Sprite sprite;

    public EnemyInd(){
        sprite = new Enemy(posX, posY, "garde");
    }

    public void resetPos() {
        posX = 16.5f;
        posY = 16.5f;
        vx = 0;
        vy = 1;
    }

    public void moveEnemy(){{
        posY += vy * moveSpeed;
        sprite.updatePos(posX, posY);
    }}

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getRotSpeed() {
        return rotSpeed;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
