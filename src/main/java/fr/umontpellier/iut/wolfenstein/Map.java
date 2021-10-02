package fr.umontpellier.iut.wolfenstein;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Map {

    private final int[][] worldMap;
    private final int width;
    private final int height;

    public Map(String url){
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
                else {
                    worldMap[i][j] = 0;
                }
            }
        }

    }

    public int[][] getWorldMap() {
        return worldMap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
