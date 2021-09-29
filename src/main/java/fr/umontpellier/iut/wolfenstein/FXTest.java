package fr.umontpellier.iut.wolfenstein;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXTest extends Application {

    private AnimationTimer game;
    private AnimationTimer game2;
    private WritableImage monImage;
    private WritableImage monImage2;
    private Scene scene;
    private final int width = 640;
    private final int height = 320;

    private final int[][] worldMap =
    {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
        {1,0,0,0,0,0,5,5,5,5,5,0,0,0,0,3,0,3,0,3,0,0,0,3},
        {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,3},
        {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,3,0,0,0,3,0,0,0,3},
        {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,3},
        {1,0,0,0,0,0,2,2,0,2,2,0,0,0,0,3,0,3,0,3,0,0,0,3},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
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
    private float posX = 12.5f;
    private float posY = 9.5f;

    // Le vecteur direction du joueur
    private float vx = -1;
    private float vy = 0;

    // Le vecteur direction de la caméra (perpendiculaire au joueur)
    private float latX = 0;
    private float latY = 1;

    private float moveSpeed = 0;
    private float rotSpeed = 0;

    // Les valeurs booléennes qui servent pour déplacer le joueur
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    private boolean isRight = false;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        GridPane root = new GridPane();

        // Image principale
        monImage = new WritableImage(width,height);
        ImageView pog = new ImageView(monImage);
        root.add(pog, 0, 0);

        // Image de debug
        monImage2 = new WritableImage(width,height);
        ImageView pog2 = new ImageView(monImage2);
        root.add(pog2, 0, 1);

        scene = new Scene(root);
        primaryStage.setTitle("Projet Wolfenstus 3D");
        primaryStage.setScene(scene);
        primaryStage.show();

        gameHandlers();
        startGame();
        copiedGame();
    }

    /**
     * La méthode changePixel permet de changer la couleur d'un pixel de l'image monImage, qui est l'affichage principal du jeu
     * @param x Les coordonnées du pixel sur l'axe des x (abscisses)
     * @param y Les coordonnées du pixel sur l'axe des y (ordonnées)
     * @param c La nouvelle couleur du pixel
     */
    private void changePixel(int x, int y, Color c){
        monImage.getPixelWriter().setColor(x, y, c);
    }

    private void startGame() {
        game = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {

                // Compteur de fps
                //System.out.println("fps = " + 1_000 / ((now - lastUpdate) / 1_000_000));

                for (int i = 0; i < width; i++) {
                    float newPosY = posY;
                    float newPosX = posX;
                    float camX = 2 * i / (float) width -1;
                    float rayDirX = vx + latX * camX;
                    float rayDirY = vy + latY * camX;


                    // La distance du mur normalisée
                    float t = 0;

                    // Les coordonnées xi et yi permettent de nous localiser dans le plan
                    int xi = (int) posX;
                    int yi = (int) posY;

                    float distX = getDist(rayDirX, newPosX);
                    float distY = getDist(rayDirY, newPosY);

                    int hit = 0;
                    int side = 0;


                    while (hit == 0) {
                        if (distX <= distY) {
                            t += distX;
                            if (rayDirX > 0){
                                xi++;
                            }
                            else {
                                xi--;
                            }
                            newPosY = newPosY + distX * rayDirY;
                            newPosX = newPosX + distX * rayDirX;
                            side = 0;
                        }
                        else {
                            t += distY;
                            if (rayDirY > 0){
                                yi++;
                            }
                            else {
                                yi--;
                            }
                            newPosY = newPosY + distY * rayDirY;
                            newPosX = newPosX + distY * rayDirX;
                            side = 1;
                        }

                        distX = getDist(rayDirX, newPosX);
                        distY = getDist(rayDirY, newPosY);

                        hit = worldMap[xi][yi];
                    }
                    int wallHeight = (int) (height / t);

                    int finToit = -wallHeight / 2 + height / 2;
                    if (finToit < 0) finToit = 0;

                    int debutSol = wallHeight / 2 + height / 2;
                    if (debutSol >= height) debutSol = height - 1;

                    for (int j = 0; j < height; j++) {
                        Color color;
                        if (j < finToit){
                            color = Color.GRAY;
                        }
                        else if (j >= debutSol){
                            color = Color.BLACK;
                        }
                        else {
                            color = chooseColor(hit);
                            if (side == 1){
                                color = color.darker();
                            }
                        }
                        changePixel(i, j, color);
                    }
                }
                // On calcule la vitesse de déplacement du joueur pour qu'elle soit constance même avec des variations de fps
                float frameTime = (now - lastUpdate) / 1_000_000_000f;
                moveSpeed = frameTime * 5;
                rotSpeed = frameTime * 3;
                moveCharacter();

                // On actualise la variable qui stocke le moment d'exécution de l'ancienne boucle
                lastUpdate = now;

                //this.stop();
            }
        };
        game.start();
    }


    /**
     * Cette méthode permet de savoir de quel couleur est le mur à dessiner
     * @param hit L'identifiant du mur dans la matrice worldMap
     * @return La couleur du mur
     */
    private Color chooseColor(int hit){
        switch (hit) {
            case 1:
                return Color.RED;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.WHITE;
            default:
                return Color.YELLOW;
        }
    }


    /**
     * Cette methode calcule la distance du joueur par rapport au mur en fonction de v sur un axe en particulier
     * @param rayDir Il s'agit de la direction du rayon de vision sur l'axe donné
     * @param pos Il s'agit de la position du joueur sur l'axe donné
     * @return La distance normalisée du prochain mur
     */
    private float getDist(float rayDir, float pos){
        float dist;
        float delta;
        if (rayDir == 0) {
            dist = 1000000000000000000f;
        } else {
            if (rayDir > 0) {
                delta = 1 - (pos % 1);
            } else {
                delta = pos % 1;
            }
            if (delta == 0 && rayDir < 0){
                delta = 1;
            }
            dist = Math.abs(delta / rayDir);
        }
        return dist;
    }


    /**
     * Cette méthode permet de lire les inputs du joueur sur son clavier afin de faire fonctionner le mouvement de la caméra première personne.
     * L'évènement KEY_PRESSED permet de savoir quand une touche du clavier est appuyée, et active les boolean respectifs.
     * L'évènement KEY_RELEASED détecte le moment où les touches sont relachées, et désactive les boolean respectifs.
     */
    private void gameHandlers() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode code = key.getCode();
            if(code == KeyCode.LEFT || code == KeyCode.Q) {
                isLeft = true;
            }
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                isRight = true;
            }
            else if ((code == KeyCode.UP || code == KeyCode.Z) && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
                isUp = true;
            }
            else if ((code == KeyCode.DOWN || code == KeyCode.S) && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
                isDown = true;
            }
            else if (code == KeyCode.ESCAPE){
                game.stop();
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            KeyCode code = key.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.Q) {
                isLeft = false;
            }
            else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                isRight = false;
            }
            else if ((code == KeyCode.UP || code == KeyCode.Z) && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
                isUp = false;
            }
            else if ((code == KeyCode.DOWN || code == KeyCode.S) && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
                isDown = false;
            }
        });
    }

    /**
     * Cette méthode est appelée à chaque frame pour faire bouger le joueur selon les boolean de déplacement activés ou non par les touches du clavier.
     * On vérifie les états des boolean, et on tourne la caméra/ déplace le joueur en fonction de leur valeurs.
     */
    private void moveCharacter(){
        if(isRight) {
            float oldVx = vx;
            vx = (float) (vx * Math.cos(-rotSpeed) - vy * Math.sin(-rotSpeed));
            vy = (float) (oldVx * Math.sin(-rotSpeed) + vy * Math.cos(-rotSpeed));
            float oldLatx = latX;
            latX = (float) (latX * Math.cos(-rotSpeed) - latY * Math.sin(-rotSpeed));
            latY = (float) (oldLatx * Math.sin(-rotSpeed) + latY * Math.cos(-rotSpeed));
        }
        else if (isLeft) {
            float oldVx = vx;
            vx = (float) (vx * Math.cos(rotSpeed) - vy * Math.sin(rotSpeed));
            vy = (float) (oldVx * Math.sin(rotSpeed) + vy * Math.cos(rotSpeed));
            float oldLatx = latX;
            latX = (float) (latX * Math.cos(rotSpeed) - latY * Math.sin(rotSpeed));
            latY = (float) (oldLatx * Math.sin(rotSpeed) + latY * Math.cos(rotSpeed));
        }
        else if (isUp && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
            if (worldMap[(int)(posX + vx * moveSpeed)][(int)posY] == 0) posX += vx * moveSpeed;
            if (worldMap[(int)posX][(int)(posY + vy * moveSpeed)] == 0) posY += vy * moveSpeed;
        }
        else if (isDown && posX + vx * moveSpeed >= 0 && posX + vx * moveSpeed < 24 && posY + vy * moveSpeed >= 0 && posY + vy * moveSpeed < 24) {
            if (worldMap[(int)(posX - vx * moveSpeed)][(int)posY] == 0) posX -= vx * moveSpeed;
            if (worldMap[(int)posX][(int)(posY - vy * moveSpeed)] == 0) posY -= vy * moveSpeed;
        }
    }

    /**
     * N'existe que pour des fins de debug. À supprimer dès que startGame() fonctionne
     */
    private void changePixelBis(int x, int y, Color c){
        monImage2.getPixelWriter().setColor(x, y, c);
    }

    /**
     * N'existe que pour des fins de debug. À supprimer dès que startGame() fonctionne
     */
    private void copiedGame() {
        game2 = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                for (int i = 0; i < width; i++) {
                    float camX = 2 * i / (float) width - 1;
                    float rayDirX = vx + latX * camX;
                    float rayDirY = vy + latY * camX;

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
                //moveCharacter();
                lastUpdate = now;
                //this.stop();
            }
        };
        game2.start();
    }
}
