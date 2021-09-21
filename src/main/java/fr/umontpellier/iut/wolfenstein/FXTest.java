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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXTest extends Application {

    private WritableImage monImage;
    private Scene scene;
    private GraphicsContext context;
    private Pane root;
    private Pane layerPane;
    private Canvas canvas;


    private AnimationTimer border;
    private AnimationTimer game;
    private int directX = 1;
    private int directY = 0;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        root = new Pane();
        monImage = new WritableImage(32,32);
        layerPane = new Pane();
        canvas = new Canvas(320, 320);
        context = canvas.getGraphicsContext2D();
        //context.scale(10, 10);
        //context.setImageSmoothing(false);
        layerPane.getChildren().add(canvas);
        root.getChildren().add(canvas);
        root.setStyle("-fx-background-color: red");
        scene = new Scene(root, 320, 320);
        primaryStage.setTitle("Test JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        //makeBorder();
        //startGame();
        //addHandlers();
        context.setFill(Color.BLUE);
        context.fillRect(0, 0, 160, 160);
    }

    private void changePixel(int x, int y, Color c){
        monImage.getPixelWriter().setColor(x, y, c);
        context.drawImage(monImage, 0, 0);
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

    private void startGame(){
        game = new AnimationTimer() {
            private final int startx = 2;
            private final int starty = 2;
            private int x = startx;
            private int y = starty;
            private long lastUpdate;
            @Override
            public void handle(long now) {
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
            }
        };
        game.start();
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
                directX = 0;
                directY = -1;
            } else if (key.getCode()== KeyCode.DOWN) {
                directX = 0;
                directY = 1;
            } else if (key.getCode()== KeyCode.ESCAPE){
                game.stop();
            }
        });
    }
}
