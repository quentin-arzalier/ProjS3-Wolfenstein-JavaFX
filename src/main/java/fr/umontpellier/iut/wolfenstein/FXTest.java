package fr.umontpellier.iut.wolfenstein;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class FXTest extends Application {



        /*
    private AnimationTimer border;
    private AnimationTimer snake;
    private AnimationTimer wall;

    private int directX = 1;
    private int directY = 0;
    private int wallSize = 10;
    */

    private AnimationTimer game;
    private AnimationTimer game2;
    private WritableImage monImage;
    private WritableImage monImage2;
    private Scene scene;
    private final int width = 320;
    private final int height = 200;

    private final int[][] worldMap =
    {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
        {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,3,0,0,0,3,0,0,0,1},
        {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,2,2,0,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,0,0,0,0,5,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,0,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // La position du joueur dans le quadrillage
    private float posX = 8;
    private float posY = 6;

    // Le vecteur direction du joueur
    private float vx = 0.80388105f;
    private float vy = 0.59479f;

    private float latX = 0;
    private float latY = 1;

    private float moveSpeed = 0;
    private float rotSpeed = 0;
    private EventHandler<KeyEvent> curr = null;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        GridPane root = new GridPane();
        monImage = new WritableImage(width,height);
        monImage2 = new WritableImage(width,height);
        ImageView pog = new ImageView(monImage);
        ImageView pog2 = new ImageView(monImage2);
        root.add(pog, 0, 0);
        root.add(pog2, 1, 0);
        scene = new Scene(root);
        primaryStage.setTitle("Test JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        startGame();
        copiedGame();
    }

    private void changePixel(int x, int y, Color c){
        monImage.getPixelWriter().setColor(x, y, c);
    }

    private void changePixelBis(int x, int y, Color c){
        monImage2.getPixelWriter().setColor(x, y, c);
    }

    private void startGame() {
        game = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                System.out.println("fps = " + 1_000 / ((now - lastUpdate) / 1_000_000));
                for (int i = 0; i < width; i++) {
                    float camX = 2 * i / (float) width - 1;
                    float rayDirX = vx + latX * camX;
                    float rayDirY = vy + latY * camX;

                    // La distance du joueur au quadrillage pour chaque vecteur du plan
                    float dX;
                    float dY;

                    // La distance du mur par rapport au vecteur camera
                    float distX;
                    float distY;

                    float t = 0;
                    int xi = (int) posX;
                    int yi = (int) posY;
                    if (rayDirX == 0) {
                        distX = 1000000000000000000f;
                    } else {
                        if (rayDirX > 0) {
                            dX = 1 - (posX % 1);
                        } else {
                            dX = posX % 1;
                        }
                        if (dX == 0){
                            dX = 1;
                        }
                        distX = Math.abs(dX / rayDirX);
                    }

                    if (rayDirY == 0) {
                        distY = 1000000000000000000f;
                    } else {
                        if (rayDirY > 0) {
                            dY = 1 - (posY % 1);
                        } else {
                            dY = posY % 1;
                        }
                        if (dY == 0){
                            dY = 1;
                        }
                        distY = Math.abs(dY / rayDirY);
                    }
                    int hit = 0;
                    int side = 0;

                    while (hit == 0) {
                        if (distX < distY) {
                            t += distX;
                            xi++;
                            dY = distX * vy;
                            dX = 1;
                            side = 0;
                        } else {
                            t += distY;
                            yi++;
                            dX = distY * vx;
                            dY = 1;
                            side = 1;
                        }
                        if (rayDirX != 0) {
                            distX = Math.abs(dX / rayDirX);
                        }
                        if (rayDirY != 0) {
                            distY = Math.abs(dY / rayDirY);
                        }
                        hit = worldMap[xi][yi];
                    }
                    int wallHeight = (int) (height / t);

                    int finToit = -wallHeight / 2 + height / 2;
                    if (finToit < 0) finToit = 0;

                    int debutSol = wallHeight / 2 + height / 2;
                    if (debutSol >= height) debutSol = height - 1;

                    Color color;
                    switch (hit) {
                        case 1:
                            color = Color.RED;
                            break; //red
                        case 2:
                            color = Color.GREEN;
                            break; //green
                        case 3:
                            color = Color.BLUE;
                            break; //blue
                        case 4:
                            color = Color.WHITE;
                            break; //white
                        default:
                            color = Color.YELLOW;
                            break; //yellow
                    }
                    if (side == 1){
                        color = color.darker();
                    }
                    for (int j = 0; j < height; j++) {
                        Color colorb;
                        if (j < finToit){
                            colorb = Color.GRAY;
                        }
                        else if (j >= debutSol){
                            colorb = Color.BLACK;
                        }
                        else {
                            colorb = color;
                        }
                        changePixel(i, j, colorb);
                    }
                }
                float frameTime = (now - lastUpdate) / 1_000_000_000f;
                moveSpeed = frameTime * 5;
                rotSpeed = frameTime * 3;
                gameHandlers();
                lastUpdate = now;
            }
        };
        game.start();
    }

    private void gameHandlers() {
        EventHandler<KeyEvent> old = curr;
        if (old != null) {
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, old);
        }
        curr = (key) -> {
            if(key.getCode()== KeyCode.LEFT) {
                float oldVx = vx;
                vx = (float) (vx * Math.cos(-rotSpeed) - vy * Math.sin(-rotSpeed));
                vy = (float) (oldVx * Math.sin(-rotSpeed) + vy * Math.cos(-rotSpeed));
                float oldLatx = latX;
                latX = (float) (latX * Math.cos(-rotSpeed) - latY * Math.sin(-rotSpeed));
                latY = (float) (oldLatx * Math.sin(-rotSpeed) + latY * Math.cos(-rotSpeed));
            }
            else if (key.getCode()== KeyCode.RIGHT) {
                float oldVx = vx;
                vx = (float) (vx * Math.cos(rotSpeed) - vy * Math.sin(rotSpeed));
                vy = (float) (oldVx * Math.sin(rotSpeed) + vy * Math.cos(rotSpeed));
                float oldLatx = latX;
                latX = (float) (latX * Math.cos(rotSpeed) - latY * Math.sin(rotSpeed));
                latY = (float) (oldLatx * Math.sin(rotSpeed) + latY * Math.cos(rotSpeed));
            }
            else if (key.getCode()== KeyCode.UP && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
                if (worldMap[(int)(posX + vx * moveSpeed)][(int)posY] == 0) posX += vx * moveSpeed;
                if (worldMap[(int)posX][(int)(posY + vy * moveSpeed)] == 0) posY += vy * moveSpeed;
            }
            else if (key.getCode()== KeyCode.DOWN && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
                if (worldMap[(int)(posX - vx * moveSpeed)][(int)posY] == 0) posX -= vx * moveSpeed;
                if (worldMap[(int)posX][(int)(posY - vy * moveSpeed)] == 0) posY -= vy * moveSpeed;
            }
            else if (key.getCode()== KeyCode.ESCAPE){
                game.stop();
            }
        };
        scene.addEventHandler(KeyEvent.KEY_PRESSED, curr);
    }

    private void copiedGame() {
        game2 = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                for (int i = 0; i < width; i++) {
                    float camX = 2 * i / (float) width - 1;
                    float rayDirX = vx + latX * camX;
                    float rayDirY = vy + latY * camX;

                    // La distance du joueur au quadrillage pour chaque vecteur du plan
                    float dX;
                    float dY;

                    // La distance du mur par rapport au vecteur camera
                    float distX;
                    float distY;

                    float t = 0;
                    int mapX = (int) posX;
                    int mapY = (int) posY;

                    float deltaX = (rayDirX == 0 ) ? 10000000000f : Math.abs(1 / rayDirX);
                    float deltaY = (rayDirY == 0 ) ? 10000000000f : Math.abs(1 / rayDirY);

                    float perpWallDist;

                    int stepX;
                    int stepY;

                    int hit = 0; //was there a wall hit?
                    int side = 0; //was a NS or a EW wall hit?
                    //calculate step and initial sideDist
                    if(rayDirX < 0)
                    {
                        stepX = -1;
                        distX = (posX - mapX) * deltaX;
                    }
                    else
                    {
                        stepX = 1;
                        distX = (mapX + 1 - posX) * deltaX;
                    }
                    if(rayDirY < 0)
                    {
                        stepY = -1;
                        distY = (posY - mapY) * deltaY;
                    }
                    else
                    {
                        stepY = 1;
                        distY = (mapY + 1 - posY) * deltaY;
                    }
                    //perform DDA
                    while(hit == 0)
                    {
                        //jump to next map square, either in x-direction, or in y-direction
                        if(distX < distY)
                        {
                            distX += deltaX;
                            mapX += stepX;
                            side = 0;
                        }
                        else
                        {
                            distY += deltaY;
                            mapY += stepY;
                            side = 1;
                        }
                        //Check if ray has hit a wall
                        if(worldMap[mapX][mapY] > 0) hit = 1;
                    }
                    //Calculate distance projected on camera direction. This is the shortest distance from the point where the wall is
                    //hit to the camera plane. Euclidean to center camera point would give fisheye effect!
                    //This can be computed as (mapX - posX + (1 - stepX) / 2) / rayDirX for side == 0, or same formula with Y
                    //for size == 1, but can be simplified to the code below thanks to how sideDist and deltaDist are computed:
                    //because they were left scaled to |rayDir|. sideDist is the entire length of the ray above after the multiple
                    //steps, but we subtract deltaDist once because one step more into the wall was taken above.
                    if(side == 0) perpWallDist = (distX - deltaX);
                    else          perpWallDist = (distY - deltaY);

                    int wallHeight = (int) (height / perpWallDist);

                    int finToit = -wallHeight / 2 + height / 2;
                    if (finToit < 0) finToit = 0;

                    int debutSol = wallHeight / 2 + height / 2;
                    if (debutSol >= height) debutSol = height - 1;

                    Color color;
                    switch (worldMap[mapX][mapY]) {
                        case 1:
                            color = Color.RED;
                            break; //red
                        case 2:
                            color = Color.GREEN;
                            break; //green
                        case 3:
                            color = Color.BLUE;
                            break; //blue
                        case 4:
                            color = Color.WHITE;
                            break; //white
                        default:
                            color = Color.YELLOW;
                            break; //yellow
                    }
                    if (side == 1) {
                        color = color.darker();
                    }
                    for (int j = 0; j < height; j++) {
                        Color colorB;
                        if (j < finToit){
                            colorB = Color.GRAY;
                        }
                        else if (j >= debutSol){
                            colorB = Color.BLACK;
                        }
                        else {
                            colorB = color;
                        }
                        changePixelBis(i, j, colorB);
                    }
                }
                float frameTime = (now - lastUpdate) / 1_000_000_000f;
                moveSpeed = frameTime * 5;
                rotSpeed = frameTime * 3;
                gameHandlers();
                lastUpdate = now;
            }
        };
        game2.start();
    }
}
