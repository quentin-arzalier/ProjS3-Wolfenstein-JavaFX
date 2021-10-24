package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.graphismes.Enemy;
import fr.umontpellier.iut.wolfenstein.graphismes.Sprite;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Map {

    private final int[][] worldMap;
    private final int width;
    private final int height;
    private ArrayList<Sprite> sprites;

    public Map(String url){
        sprites = new ArrayList<>();
        Image maMap = new Image(url);
        PixelReader reader = maMap.getPixelReader();
        width = (int) maMap.getWidth();
        height = (int) maMap.getHeight();
        worldMap = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color maCoul = reader.getColor(i, j);
                if (maCoul.equals(Color.RED)){
                    worldMap[i][j] = 1;
                }
                else if (maCoul.equals(Color.GREEN)){
                    worldMap[i][j] = 2;
                }
                else if (maCoul.equals(Color.BLUE)){
                    worldMap[i][j] = 3;
                }
                else if (maCoul.equals(Color.WHITE)){
                    worldMap[i][j] = 4;
                }
                else if (maCoul.equals(Color.YELLOW)){
                    worldMap[i][j] = 5;
                }
                else if (maCoul.equals(Color.PURPLE)){
                    worldMap[i][j] = 6;
                }
                else if (maCoul.equals(Color.web("#CCFFFF"))){
                    worldMap[i][j] = 7;
                }
                else if (maCoul.equals(Color.web("#33CCCC"))){
                    worldMap[i][j] = 8;
                }
                else if (maCoul.equals(Color.web("#CCFFCC"))){
                    worldMap[i][j] = 9;
                }
                else if (maCoul.equals(Color.web("#99CCFF"))){
                    worldMap[i][j] = 10;
                }
                else if (maCoul.equals(Color.SADDLEBROWN)){
                    sprites.add(new Sprite(i+0.5f, j+0.5f, "barrel"));
                    worldMap[i][j] = 0;
                }
                else if (maCoul.equals(Color.LIGHTGRAY)){
                    sprites.add(new Sprite(i+0.5f, j+0.5f, "pillar"));
                    worldMap[i][j] = 0;
                }
                else if (maCoul.equals(Color.LIME)){
                    sprites.add(new Sprite(i+0.5f, j+0.5f, "light"));
                    worldMap[i][j] = 0;
                }
                else if (maCoul.equals(Color.AQUA)){
                    sprites.add(new Enemy(i+0.5f, j+0.5f, "garde"));
                    worldMap[i][j] = 0;
                }
                else {
                    worldMap[i][j] = 0;
                }
            }
        }
        for (Sprite sprite : sprites){
            sprite.setWorldmap(this.worldMap);
        }
    }

    public int[][] getWorldMap() {
        return worldMap;
    }

    public ArrayList<Sprite> getSprites() {
        return sprites;
    }

    public void addSprite(Sprite s){
        this.sprites.add(s);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
