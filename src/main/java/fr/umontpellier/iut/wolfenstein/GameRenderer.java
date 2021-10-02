package fr.umontpellier.iut.wolfenstein;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GameRenderer extends ImageView {

    private int[][] worldMap;
    private WritableImage monImage;
    private final Player currPlayer;
    private final int width;
    private final int height;
    private final Minimap minimap;
    private final int texSize;

    private AnimationTimer renderer;

    public GameRenderer(Player p, Minimap map){
        currPlayer = p;
        width = 480;
        height = 360;
        texSize = 64;
        minimap = map;
        this.setFitWidth(960);
        this.setFitHeight(600);

        dispLoop();
    }

    public void setMap(Map map){
        renderer.stop();
        monImage = new WritableImage(width, height);
        setImage(monImage);
        worldMap = map.getWorldMap();
        renderer.start();
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

    /**
     * La méthode principale, s'occupant des algorithme de détection des murs et appelle les méthode de graphisme
     */
    private void dispLoop() {
        renderer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {

                // Compteur de fps
                System.out.println("fps = " + 1_000 / ((now - lastUpdate) / 1_000_000));
                float posX = currPlayer.getPosX();
                float posY = currPlayer.getPosY();
                float vx = currPlayer.getVx();
                float vy = currPlayer.getVy();
                float latX = currPlayer.getLatX();
                float latY = currPlayer.getLatY();

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

                    int X;
                    if(side==1) X = (int) (newPosX%1 * texSize);
                    else X = (int) (newPosY%1 * texSize);
                    double Y = 0;
                    if(finToit==0) Y = Math.abs(height-wallHeight)/2;



                    for (int j = 0; j < height; j++) {
                        Color color;
                        if (j < finToit){
                            color = Color.web("383838");
                        }
                        else if (j >= debutSol){
                            color = Color.web("#707070");
                        }
                        else {
                            if(Y>=64) Y = 63;
                            color = chooseColor(
                                    hit,
                                    side,
                                    X,
                                    (int) Y
                            );
                            Y+=(double) texSize/(double) wallHeight;

                        }
                        changePixel(i, j, color);
                    }
                }

                // On calcule la vitesse de déplacement du joueur pour qu'elle soit constance même avec des variations de fps
                float frameTime = (now - lastUpdate) / 1_000_000_000f;
                currPlayer.setMoveSpeed(frameTime * 5);
                currPlayer.setRotSpeed(frameTime * 3);
                currPlayer.moveCharacter(worldMap);

                // On actualise la variable qui stocke le moment d'exécution de l'ancienne boucle
                lastUpdate = now;
                minimap.update(posX, posY, vx, vy, latX, latY);
            }
        };
    }

    /**
     * Cette méthode permet de savoir de quel couleur est le mur à dessiner
     * @param hit L'identifiant du mur dans la matrice worldMap
     * @return La couleur du mur
     */
    private Color chooseColor(int hit,int side,int X,int Y){
        return MurType.getById(hit).getTexs(side).getPixelReader().getColor(X,Y);
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

    public void stop() {
        renderer.stop();
    }
}
