package fr.umontpellier.iut.wolfenstein;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Minimap extends StackPane {

    private final GraphicsContext contextBG;
    private final GraphicsContext contextPlayer;
    private final Text fpsCounter;
    private Image currImage;
    private float currScaleX;
    private float currScaleY;
    private ArrayList<Player> joueurs;

    public Minimap(){
        joueurs = new ArrayList<>();
        this.setMaxWidth(600);
        this.setMaxHeight(600);
        Canvas background = new Canvas(600, 600);
        contextBG = background.getGraphicsContext2D();
        contextBG.setImageSmoothing(false);

        Canvas playerPane = new Canvas(600, 600);
        currScaleX = 1;
        currScaleY = 1;
        contextPlayer = playerPane.getGraphicsContext2D();

        Font fpsFont = new Font("Verdana", 20);
        fpsCounter = new Text("FPS : XX");
        fpsCounter.setStroke(Color.YELLOW);
        fpsCounter.setFill(Color.YELLOW);
        fpsCounter.setFont(fpsFont);
        StackPane.setAlignment(fpsCounter, Pos.TOP_RIGHT);
        StackPane.setMargin(fpsCounter, new Insets(5));

        this.getChildren().add(background);
        this.getChildren().add(playerPane);
        this.getChildren().add(fpsCounter);
    }

    public void setMap(String s){
        contextBG.scale(1/currScaleX, 1/currScaleY);
        contextBG.clearRect(0, 0, 600, 600);
        currImage = new Image(s);
        currScaleX = (float) (600f/currImage.getWidth());
        currScaleY = (float) (600f/currImage.getHeight());
        contextBG.scale(currScaleX, currScaleY);
        contextBG.drawImage(currImage, 0, 0);
    }

    public void addJoueur(Player p){
        joueurs.add(p);
    }

    public void update(long fps) {
        contextPlayer.clearRect(0, 0, 600, 600);
        fpsCounter.setText(Long.toString(fps));
        contextBG.drawImage(currImage, 0, 0);
        for (Player p : joueurs){
            float posX = p.getPosX();
            float posY = p.getPosY();
            float vx = p.getVx();
            float vy = p.getVy();
            float latX = p.getLatX();
            float latY = p.getLatY();

            int pixelPosX = (int) (posX * 600 / currImage.getWidth());
            int pixelPosY = (int) (posY * 600 / currImage.getHeight());

            double angle1X = (posX + vx*3 - latX*1.5) * 600 / currImage.getWidth();
            double angle1Y = (posY + vy*3 - latY*1.5) * 600 / currImage.getHeight();
            double angle2X = (posX + vx*3 + latX*1.5) * 600 / currImage.getWidth();
            double angle2Y = (posY + vy*3 + latY*1.5) * 600 / currImage.getHeight();
            contextPlayer.setFill(new Color(0.8, 0.8, 0.0, 0.6));
            contextPlayer.strokePolygon(new double[]{pixelPosX, angle1X, angle2X}, new double[]{pixelPosY, angle1Y, angle2Y}, 3);
            contextPlayer.fillPolygon(new double[]{pixelPosX, angle1X, angle2X}, new double[]{pixelPosY, angle1Y, angle2Y}, 3);

            contextPlayer.setFill(p.getColor());
            contextPlayer.strokeOval(pixelPosX-3, pixelPosY-3, 6, 6);
            contextPlayer.fillOval(pixelPosX-3, pixelPosY-3, 6, 6);
        }
    }
}
