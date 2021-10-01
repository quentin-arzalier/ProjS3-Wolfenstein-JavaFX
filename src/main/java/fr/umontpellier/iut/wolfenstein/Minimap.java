package fr.umontpellier.iut.wolfenstein;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Minimap extends StackPane {

    private final GraphicsContext contextBG;
    private final GraphicsContext contextPlayer;
    private Image currImage;
    private float currScaleX;
    private float currScaleY;

    public Minimap(){
        Canvas background = new Canvas(600, 600);
        contextBG = background.getGraphicsContext2D();
        contextBG.setImageSmoothing(false);

        Canvas playerPane = new Canvas(600, 600);
        currScaleX = 1;
        currScaleY = 1;
        contextPlayer = playerPane.getGraphicsContext2D();
        this.getChildren().add(background);
        this.getChildren().add(playerPane);
    }

    public void setMap(String s){
        contextBG.scale(1/currScaleX, 1/currScaleY);
        currImage = new Image(s);
        currScaleX = (float) (600f/currImage.getWidth());
        currScaleY = (float) (600f/currImage.getHeight());
        contextBG.scale(currScaleX, currScaleY);
        contextBG.drawImage(currImage, 0, 0);
    }

    public void update(float posX, float posY, float vx, float vy, float latX, float latY) {
        contextBG.drawImage(currImage, 0, 0);

        int pixelPosX = (int) (posX * 600 / currImage.getWidth());
        int pixelPosY = (int) (posY * 600 / currImage.getHeight());
        contextPlayer.clearRect(0, 0, 600, 600);
        contextPlayer.setFill(Color.AQUA);
        contextPlayer.strokeOval(pixelPosX-5, pixelPosY-5, 5, 5 );
        contextPlayer.fillOval(pixelPosX-5, pixelPosY-5, 5, 5);

        double angle1X = (posX + vx*3 - latX*1.5) * 600 / currImage.getWidth();
        double angle1Y = (posY + vy*3 - latY*1.5) * 600 / currImage.getHeight();
        double angle2X = (posX + vx*3 + latX*1.5) * 600 / currImage.getWidth();
        double angle2Y = (posY + vy*3 + latY*1.5) * 600 / currImage.getHeight();
        contextPlayer.setFill(new Color(0.8, 0.8, 0.0, 0.6));
        contextPlayer.strokePolygon(new double[]{pixelPosX, angle1X, angle2X}, new double[]{pixelPosY, angle1Y, angle2Y}, 3);
        contextPlayer.fillPolygon(new double[]{pixelPosX, angle1X, angle2X}, new double[]{pixelPosY, angle1Y, angle2Y}, 3);
    }
}
