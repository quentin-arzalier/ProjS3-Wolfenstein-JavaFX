package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.graphismes.Enemy;
import fr.umontpellier.iut.wolfenstein.graphismes.Sprite;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * La classe Map permet de générer la matrice des murs utilisée par la logique du jeu ainsi que par les méthodes d'affichage de GameRenderer.
 * Elle permet aussi d'ajouter de charger les différents sprites, qui ne sont pas représentés dans la matrice worldMap (qui ne représente donc que les murs)
 */
public class    Map {

    private final int[][] worldMap;
    private final int width;
    private final int height;
    private ArrayList<Sprite> sprites;

    public Map(String url){
        sprites = new ArrayList<>();
        Image maMap = new Image(url);                // L'image utilisée pour générer la map est également utilisée pour l'affichage de la minimap
        PixelReader reader = maMap.getPixelReader(); // Le pixelReader permet de lire la valeur RGB d'un pixel.
        width = (int) maMap.getWidth();
        height = (int) maMap.getHeight();
        worldMap = new int[width][height];

        // Cette boucle parcourt chaque pixel du dessin et lit sa valeur RGB. Si la couleur est comprise, on fait ce qu'il faut.
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
                else if (maCoul.equals(Color.BLACK)) {
                    worldMap[i][j] = 0;
                }
                else {
                    worldMap[i][j] = 99;
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
