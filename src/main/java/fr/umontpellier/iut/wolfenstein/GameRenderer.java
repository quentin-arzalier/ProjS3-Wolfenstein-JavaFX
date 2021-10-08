package fr.umontpellier.iut.wolfenstein;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class GameRenderer extends Pane {

    private int[][] worldMap;


    private final WritableImage monImage;
    private final GraphicsContext context;
    private final Player currPlayer;
    private final Minimap minimap;

    private final int texSize = 64;
    private final int drawWidth = 480;
    private final int drawHeight = 300;
    private final int realWidth = 480;
    private final int realHeight = 300;

    private AnimationTimer renderer;

    public GameRenderer(Player p, Minimap map){
        Canvas base = new Canvas(drawWidth, drawHeight);
        base.setStyle("-fx-background-color: magenta");
        currPlayer = p;
        minimap = map;
        context = base.getGraphicsContext2D();
        this.getChildren().add(base);
        context.scale(drawWidth /(float) realWidth, drawHeight /(float) realHeight);
        context.setImageSmoothing(false);
        monImage = new WritableImage(realWidth, realHeight);
        dispLoop();
    }

    public void setMap(Map map){
        renderer.stop();
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
            private long lastCheck = 0;
            private long fps = 0;

            @Override
            public void handle(long now) {
                float posX = currPlayer.getPosX();
                float posY = currPlayer.getPosY();
                float vx = currPlayer.getVx();
                float vy = currPlayer.getVy();
                float latX = currPlayer.getLatX();
                float latY = currPlayer.getLatY();

                for (int i = 0; i < realWidth; i++) {
                    float camX = 2 * i / (float) realWidth -1;
                    float rayDirX = vx + latX * camX;
                    float rayDirY = vy + latY * camX;

                    HashMap<String, Number> ddaInfo = startDDA(rayDirX, rayDirY, posX, posY);

                    float newPosX = ddaInfo.get("newPosX").floatValue();
                    float newPosY = ddaInfo.get("newPosY").floatValue();
                    int wallHeight =  ddaInfo.get("wallHeight").intValue();
                    int hit = ddaInfo.get("hit").intValue();
                    int side = ddaInfo.get("side").intValue();


                    int X;
                    float Y = 0;
                    float pixelPos = (side == 1) ? newPosX : newPosY;

                    X = (int) ((pixelPos%1) * texSize);
                    // La formule pour lire la texture dans l'autre sens est :  X = texSize - X - 1;

                    if(wallHeight >= realHeight) Y = (float)(wallHeight - realHeight)/2f/(float)wallHeight * texSize;


                    int finToit = -wallHeight / 2 + realHeight / 2;
                    if (finToit < 0) finToit = 0;

                    int debutSol = wallHeight / 2 + realHeight / 2;
                    if (debutSol >= realHeight) debutSol = realHeight - 1;

                    for (int j = 0; j < realHeight; j++) {
                        Color color;
                        if (j < finToit){
                            color = Color.web("383838");
                        }
                        else if (j >= debutSol){
                            color = Color.web("#707070");
                        }
                        else {
                            color = chooseColor(hit, side, X, (int) Y);
                            Y += texSize /(double) wallHeight;
                        }
                        changePixel(i, j, color);
                    }
                }
                context.drawImage(monImage, 0, 0);

                // On calcule la vitesse de déplacement du joueur pour qu'elle soit constance même avec des variations de fps
                float frameTime = (now - lastUpdate) / 1_000_000_000f;
                currPlayer.setMoveSpeed(frameTime * 5);
                currPlayer.setRotSpeed(frameTime * 3);
                currPlayer.moveCharacter(worldMap);
                if (now - lastCheck >= 1_000_000_000) {
                    fps = 1_000 / ((now - lastUpdate) / 1_000_000);
                    lastCheck = now;
                }
                minimap.update(fps);

                // On actualise la variable qui stocke le moment d'exécution de l'ancienne boucle
                lastUpdate = now;
            }
        };
    }

    /**
     * Cette méthode permet de savoir de quel couleur est le mur à dessiner
     * @param hit L'identifiant du mur dans la matrice worldMap
     * @return La couleur du mur
     */
    private Color chooseColor(int hit,int side,int X,int Y){
        return MurType.getById(hit).getText(side).getPixelReader().getColor(X,Y);
    }

    /**
     * L'algorithme permettant de calculer la distance d'un mur par rapport au joueur
     * @param rayDirX Les coordonnées X du vecteur vision actuel
     * @param rayDirY Les coordonnées Y du vecteur vision actuel
     * @param posX Les coordonnées X du joueur
     * @param posY Les coordonnées Y du joueur
     * @return Une Map contenant toutes les infos nécessaires au remplissage de l'image, identifiées par leur nom
     */
    private HashMap<String, Number> startDDA(float rayDirX, float rayDirY, float posX, float posY){
        double distX = getDist(rayDirX, posX);
        double distY = getDist(rayDirY, posY);
        int xi = (int) posX;
        int yi = (int) posY;
        double newPosX = posX;
        double newPosY = posY;

        int hit = 0;
        int side = 0;
        double t = 0;

        // Algorithme de détection des murs
        int i = 0;
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
            i++;
        }

        HashMap<String, Number> retour = new HashMap<>();
        retour.put("hit", hit);
        Number wallHeight = realHeight / t;
        if (wallHeight.doubleValue() % 1 > 0.9999999999){
            wallHeight = (int) (realHeight / t) + 1;
        }
        else {
            wallHeight = (int) (realHeight / t);
        }

        retour.put("wallHeight", wallHeight);
        retour.put("side", side);
        retour.put("newPosX", newPosX);
        retour.put("newPosY", newPosY);

        return retour;
    }


    /**
     * Cette methode calcule la distance du joueur par rapport au mur en fonction de v sur un axe en particulier
     * @param rayDir Il s'agit de la direction du rayon de vision sur l'axe donné
     * @param pos Il s'agit de la position du joueur sur l'axe donné
     * @return La distance normalisée du prochain mur
     */
    private double getDist(double rayDir, double pos){
        double dist;
        double delta;
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
            //System.out.println("delta = " + delta + ", rayDir = " + rayDir + ", pos = " + pos);
        }
        return dist;
    }
}
