package fr.umontpellier.iut.wolfenstein;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXTest extends Application {

    private WritableImage monImage;
    private Scene scene;


    private AnimationTimer border;
    private AnimationTimer snake;
    private AnimationTimer wall;
    private int directX = 1;
    private int directY = 0;
    private int wallSize = 10;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        Pane root = new Pane();
        monImage = new WritableImage(320,200);
        ImageView pog = new ImageView(monImage);
        root.getChildren().add(pog);
        scene = new Scene(root);
        primaryStage.setTitle("Test JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        //makeBorder();
        //startSnake();
        startWall();
        addHandlers();
    }

    private void changePixel(int x, int y, Color c){
        monImage.getPixelWriter().setColor(x, y, c);
    }

    private void makeBorder() {
        border = new AnimationTimer() {
            private int i = 0;
            private long lastUpdate = 0 ;
            private final Color red = Color.RED;
            @Override
            public void handle(long l) {
                if (l - lastUpdate >= 30_000_000) {
                    changePixel(i, 0, red);
                    changePixel(i, 31, red);
                    changePixel(0, i, red);
                    changePixel(31, i, red);

                    i++;
                    if (i==32){
                        border.stop();
                    }
                    lastUpdate = l;
                }
            }
        };

        border.start();
    }

    private void startSnake(){
        snake = new AnimationTimer() {
            private final int startx = 2;
            private final int starty = 2;
            private int x = startx;
            private int y = starty;
            private long lastUpdate;
            private long test;
            @Override
            public void handle(long now) {
                System.out.println("Depuis la frame précédente, " + ((now - test) / 1_000_000) + "ms se sont écoulées.");
                if (now - lastUpdate >= 50_000_000) {
                    changePixel(x,y, Color.WHITE);
                    x+=directX;
                    y+=directY;
                    if (x == 31 || x == 0 || y == 31 || y == 0){
                        x = startx;
                        y = starty;
                    } else {
                        changePixel(x,y, Color.BLACK);
                    }
                    lastUpdate = now;
                }
                test = now;
            }
        };
        snake.start();
    }

    private void startWall(){
        wall = new AnimationTimer() {

            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                //System.out.println("Depuis la frame précédente, " + ((now - lastUpdate) / 1_000_000) + "ms se sont écoulées.");
                System.out.println("fps = " + 1_000/((now-lastUpdate) / 1_000_000));
                for (int x = 0; x < 320; x++) {
                    for (int y = 0; y < 200; y++) {
                        if (y < 100 - wallSize/2) {
                            changePixel(x, y, Color.GRAY);
                        } else if (y < 100 + wallSize/2 -1) {
                            changePixel(x, y, Color.CADETBLUE);
                        } else {
                            changePixel(x, y, Color.DARKGRAY);
                        }
                    }
                }
                lastUpdate = now;
            }
        };
        wall.start();
    }

    private void addHandlers(){
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.LEFT) {
                directX = -1;
                directY = 0;
            } else if (key.getCode()== KeyCode.RIGHT) {
                directX = 1;
                directY = 0;
            } else if (key.getCode()== KeyCode.UP) {
                if (wallSize < 200){
                    wallSize++;
                }
                directX = 0;
                directY = -1;
            } else if (key.getCode()== KeyCode.DOWN) {
                if (wallSize > 0){
                    wallSize--;
                }
                directX = 0;
                directY = 1;
            } else if (key.getCode()== KeyCode.ESCAPE){
                snake.stop();
                wall.stop();
            }
        });
    }
}
