package fr.umontpellier.iut.wolfenstein.graphismes;

import fr.umontpellier.iut.wolfenstein.gameplay.MurType;
import fr.umontpellier.iut.wolfenstein.gameplay.Map;
import fr.umontpellier.iut.wolfenstein.gameplay.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GameRenderer extends Pane {

    private int[][] worldMap;
    private final float[] zBuffer;
    private ArrayList<Sprite> sprites;


    private final WritableImage monImage;
    private final GraphicsContext context;
    private final Player currPlayer;
    private final Minimap minimap;


    private final int texSize = 64;
    private final int drawWidth = 480 * 2;
    private final int drawHeight = 300 * 2;
    private final int realWidth = 480;
    private final int realHeight = 300;


    private AnimationTimer animationTimer;

    public GameRenderer(Player p, Minimap map) {
        zBuffer = new float[realWidth];
        Canvas base = new Canvas(drawWidth, drawHeight);
        currPlayer = p;
        minimap = map;
        context = base.getGraphicsContext2D();
        this.getChildren().add(base);
        context.scale(drawWidth / (float) realWidth, drawHeight / (float) realHeight);
        context.setImageSmoothing(false);
        monImage = new WritableImage(realWidth, realHeight);
        animationTimer = new WolfAnimationTimer();
    }

    public void setMap(Map map) {
        animationTimer.stop();
        worldMap = map.getWorldMap();
        sprites = map.getSprites();
        animationTimer.start();
    }

    /**
     * La méthode changePixel permet de changer la couleur d'un pixel de l'image monImage, qui est l'affichage principal du jeu
     *
     * @param x Les coordonnées du pixel sur l'axe des x (abscisses)
     * @param y Les coordonnées du pixel sur l'axe des y (ordonnées)
     * @param c La nouvelle couleur du pixel
     */
    private void changePixel(int x, int y, Color c) {
        monImage.getPixelWriter().setColor(x, y, c);
    }

    private class WolfAnimationTimer extends AnimationTimer {
        private long lastUpdate = 0;
        private long lastCheck = 0;

        /**
         * Méhode qui est appelée automatiquement à chaque frame et qui se charge de mettre à jour l'état du jeu
         * et l'affichage
         */
        @Override
        public void handle(long now) {
            // Temps écoulé depuis la dernière mise à jour (en secondes)
            float deltaTime = (now - lastUpdate) / 1_000_000_000f;
            deltaTime = Math.min(deltaTime, 0.1f);  // deltaTime ne peut pas être supérieur à 0.1 secondes

            // mise à jour des différents élements du jeu
            update(deltaTime);

            // dessine le jeu à l'écran
            draw();

            // On calcule les images par seconde une fois par seconde
            if (now - lastCheck >= 1_000_000_000) {
                long fps = 1_000_000_000 / (now - lastUpdate);
                lastCheck = now;
                minimap.update(fps);
            }
            // On actualise la variable qui stocke le moment d'exécution de l'ancienne boucle
            lastUpdate = now;
        }
    }

    /**
     * Met à jour les différents éléments du jeu à chaque frame
     * @param deltaTime temps écoulé depuis la dernière mise à jour
     */
    private void update(float deltaTime) {
        currPlayer.moveCharacter(deltaTime);
        for (Sprite sprite : sprites) {
            sprite.update(deltaTime, currPlayer);
        }
        Collections.sort(sprites);
    }

    /**
     * Méthode chargée de dessiner l'état du jeu à l'écran
     */
    private void draw() {
        // Dessine les différents éléments de l'image dans le buffer
        drawFloor();
        drawWalls();
        drawSprites();

        // On dessine le buffer des pixels à l'écran en une seule fois (réduit le lag)
        context.drawImage(monImage, 0, 0);

    }
    private void drawFloor() {

        // Informations joueur
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();
        float camPitch = currPlayer.getCamPitch();


        // La position de l'horizon
        float horizon = realHeight / 2f;


        // Le vecteur caméra le plus à gauche (x = 0)
        float camVectXG = vx + vy;
        float camVectYG = vy - vx;

        // Le vecteur caméra le plus à droite (x = realWidth)
        float camVectXD = vx - vy;
        float camVectYD = vy + vx;

        // On dessine ligne par ligne donc on itère sur l'axe Y
        for (int y = 0; y < realHeight; y++) {
            boolean isFloor = y > horizon + camPitch;

            // La position de la ligne que l'on dessine en fonction de la ligne d'horizon
            int scanLineY = y - (int) horizon - (int) camPitch;
            if (!isFloor)
                scanLineY *= -1; // Nécessaire pour que le plafond soit dessiné correctement lorsque le joueur bouge

            float rowDist = horizon / scanLineY;

            // Pas permettant de capter la position de la texture du mur/du sol
            float pasX = rowDist * (camVectXD - camVectXG) / realWidth;
            float pasY = rowDist * (camVectYD - camVectYG) / realWidth;

            // Les coordonnées réelles de la première colonne :
            float solX = posX + rowDist * camVectXG;
            float solY = posY + rowDist * camVectYG;

            // On commence le dessin ligne par ligne
            for (int x = 0; x < realWidth; x++) {
                int texX = (int) (texSize * (solX % 1)) & (texSize - 1);
                int texY = (int) (texSize * (solY % 1)) & (texSize - 1);

                solX += pasX;
                solY += pasY;

                // Les textures des murs ayant pour id 2 et 4 on été utilisées pour le sol et le plafond.
                int text = 4;
                if (isFloor) text = 2;

                Color color = chooseColor(text, 1, texX, texY);

                changePixel(x, y, color);
            }
        }


    }

    private void drawWalls() {
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();
        float camPitch = currPlayer.getCamPitch();

        for (int x = 0; x < realWidth; x++) {
            float camX = 2 * x / (float) realWidth - 1;
            float rayDirX = vx - vy * camX;
            float rayDirY = vy + vx * camX;


            HashMap<String, Number> ddaInfo = startDDA(rayDirX, rayDirY, posX, posY);


            int nbHits = ddaInfo.get("nbHits").intValue();
            float t = ddaInfo.get("t").floatValue();

            // Si on passe par des murs transparents, nbHits sera supérieur à 0. On veut parcourir la liste des murs dans le sens inverse
            for (int i = nbHits - 1; i >= 0; i--) {


                // On récupère toutes les informations importantes sur le mur que l'on veut dessiner
                float newPosX = ddaInfo.get("newPosX" + i).floatValue();
                float newPosY = ddaInfo.get("newPosY" + i).floatValue();
                int wallHeight = ddaInfo.get("wallHeight" + i).intValue();
                int hit = ddaInfo.get("hit" + i).intValue();
                int side = ddaInfo.get("side" + i).intValue();

                // Le mur d'ID 7 n'est dessiné que des côtés nord et sud et le 8 des côtés ouest et est.
                // C'est une tentative de faire des murs fins et ça rend presque bien!
                if (hit == 7 && side == 0 || hit == 8 && side == 1) {
                    continue;
                }


                // On détermine quel pixel de la texture doit être dessiné en premier (Sur quelle fraction du mur notre rayon a tapé)
                float wallTextY = 0;
                float pixelPos = (side == 1) ? newPosX : newPosY;
                int wallTextX = (int) ((pixelPos % 1) * texSize);


                //if (side == 0 && rayDirX > 0) wallTextX = texSize - wallTextX - 1;
                //if (side == 1 && rayDirY < 1) wallTextX = texSize - wallTextX - 1;

                // On calcule le pixel de hauteur ou commencer à dessiner le mur.
                int debutDessin = -wallHeight / 2 + realHeight / 2 + (int) (camPitch);

                // S'il est inférieur à 0, on ne commence pas la lecture du fichier texture tout en haut
                if (debutDessin < 0) {
                    wallTextY = -debutDessin / (float) wallHeight * texSize;
                    debutDessin = 0;
                }

                // On calcule aussi la fin du dessin et on vérifie les mêmes choses.
                int finDessin = wallHeight / 2 + realHeight / 2 + (int) (camPitch);
                if (finDessin >= realHeight) finDessin = realHeight - 1;

                for (int y = debutDessin; y < finDessin; y++) {
                    Color color = chooseColor(hit, side, wallTextX, (int) wallTextY);

                    // Si on a traversé plusieurs murs et que le mur que l'on dessine n'est pas le dernier, on doit calculer la moyenne des couleurs RGB.
                    if (nbHits > 1 && i < nbHits - 1) {
                        Color oldColor = monImage.getPixelReader().getColor(x, y);
                        color = new Color((color.getRed() + oldColor.getRed()) / 2, (color.getGreen() + oldColor.getGreen()) / 2, (color.getBlue() + oldColor.getBlue()) / 2, 1);
                    }

                    changePixel(x, y, color);
                    wallTextY += texSize / (double) wallHeight;
                }
            }
            // On enregistre la position du mur solide touché pour savoir si on dessine ou non les différents sprites.
            zBuffer[x] = t;
        }
    }


    private void drawSprites() {
        for (Sprite currSprite : sprites) {
            float spriteScreenPosX = currSprite.getLocalX();
            float spriteScreenPosY = currSprite.getLocalY();

            // Il faut désormais replacer ces valeurs sur un X de coordonnées realWidth et non mapSize
            int screenPosX = (int) ((realWidth / 2) * (1 + spriteScreenPosY / spriteScreenPosX));
            // La taille en pixels du sprite sur l'écran.
            int tailleSprite = (int) Math.abs(realHeight / spriteScreenPosX);

            int demiSprite = tailleSprite / 2;

            int gaucheSprite = screenPosX - demiSprite;
            if (gaucheSprite < 0) gaucheSprite = 0;
            int droiteSprite = screenPosX + demiSprite;
            if (droiteSprite >= realWidth) droiteSprite = realWidth - 1;

            int camPitch = (int) currPlayer.getCamPitch();
            int hautSprite = realHeight / 2 - demiSprite + camPitch;
            if (hautSprite < 0) hautSprite = 0;
            int basSprite = realHeight / 2 + demiSprite + camPitch;
            if (basSprite >= realHeight) basSprite = realHeight - 1;

            // On dessine le sprite
            if (spriteScreenPosX > 0.5) { // On ne dessine le sprite que si il se trouve au moins 0.5 unités devant le joueur
                for (int x = gaucheSprite; x < droiteSprite; x++) {

                    int texX = (x + tailleSprite / 2 - screenPosX) * texSize / tailleSprite;

                    if (spriteScreenPosX < zBuffer[x]) { // On vérifie si la colonne à dessiner se trouve bien devant un mur
                        for (int y = hautSprite; y < basSprite; y++) {

                            int texY = (y - camPitch - realHeight / 2 + tailleSprite / 2) * texSize / tailleSprite;

                            Color color = currSprite.getTex().getPixelReader().getColor(texX, texY);
                            if (color.getOpacity() != 0) {
                                changePixel(x, y, color);
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * Cette méthode permet de savoir de quel couleur est le pixel à dessiner du mur en question
     *
     * @param hit  L'identifiant du mur dans la matrice worldMap
     * @param side Le côté touché permet de déterminer si l'on doit prendre la texture plus foncée ou non.
     * @param texX La position X du pixel à lire sur le fichier texture
     * @param texY La position Y du pixel à lire sur le fichier texture
     * @return La couleur du pixel à dessiner
     */

    private Color chooseColor(int hit, int side, int texX, int texY) {
        return MurType.getById(hit).getText(side).getPixelReader().getColor(texX, texY);
    }

    /**
     * L'algorithme permettant de calculer la distance d'un mur par rapport au joueur
     * (DDA signifie Digital Differential Analyzer, ADN ou analyseur différentiel numérique en français)
     *
     * @param rayDirX Les coordonnées X du vecteur vision actuel
     * @param rayDirY Les coordonnées Y du vecteur vision actuel
     * @param posX    Les coordonnées X du joueur
     * @param posY    Les coordonnées Y du joueur
     * @return Une Map contenant toutes les infos nécessaires au remplissage de l'image, identifiées par leur nom
     */
    private HashMap<String, Number> startDDA(float rayDirX, float rayDirY, float posX, float posY) {
        double distX = getDist(rayDirX, posX);
        double distY = getDist(rayDirY, posY);
        int xi = (int) posX;
        int yi = (int) posY;
        double newPosX = posX;
        double newPosY = posY;

        int hit = 0;
        int side;
        double t = 0;
        HashMap<String, Number> retour = new HashMap<>();
        int nbHits = 0;

        // Algorithme de détection des murs
        while (hit == 0) {
            if (distX <= distY) {
                t += distX;
                if (rayDirX > 0) {
                    xi++;
                    newPosX = newPosX + (1 - newPosX % 1);
                } else {
                    xi--;
                    newPosX = newPosX - (newPosX % 1);
                }
                newPosY = newPosY + distX * rayDirY;
                side = 0;
            } else {
                t += distY;
                if (rayDirY > 0) {
                    yi++;
                    newPosY = newPosY + (1 - newPosY % 1);
                } else {
                    yi--;
                    newPosY = newPosY - (newPosY % 1);
                }
                newPosX = newPosX + distY * rayDirX;
                side = 1;
            }
            distX = getDist(rayDirX, newPosX);
            distY = getDist(rayDirY, newPosY);

            hit = worldMap[xi][yi];

            if (hit != 0) {
                double oldX = newPosX;
                double oldY = newPosY;
                if (hit == 9 || hit == 10) {
                    HashMap<String, Number> slopeInfo = getSlopeDist(rayDirX, rayDirY, newPosX, newPosY, hit, side);
                    newPosX = slopeInfo.get("newPosX").doubleValue();
                    newPosY = slopeInfo.get("newPosY").doubleValue();
                    double distance = slopeInfo.get("distance").doubleValue();
                    t += distance;
                }
                retour.put("hit" + nbHits, hit);
                retour.put("side" + nbHits, side);
                Number wallHeight = realHeight / t;
                retour.put("wallHeight" + nbHits, wallHeight);
                retour.put("newPosX" + nbHits, newPosX);
                retour.put("newPosY" + nbHits, newPosY);
                if (hit >= 7 && hit <= 10) {
                    hit = 0;
                    newPosX = oldX;
                    newPosY = oldY;
                }
                nbHits++;

            }
        }


        retour.put("t", t);
        retour.put("nbHits", nbHits);

        return retour;
    }

    /**
     * Cette methode calcule la distance du joueur par rapport au mur en fonction de v sur un axe en particulier
     *
     * @param rayDir Il s'agit de la direction du rayon de vision sur l'axe donné
     * @param pos    Il s'agit de la position du joueur sur l'axe donné
     * @return La distance normalisée du prochain mur
     */
    private double getDist(double rayDir, double pos) {
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
            if (delta == 0 && rayDir < 0) {
                delta = 1;
            }
            dist = Math.abs(delta / rayDir);
            //System.out.println("delta = " + delta + ", rayDir = " + rayDir + ", pos = " + pos);
        }
        return dist;
    }

    private HashMap<String, Number> getSlopeDist(double rayDirX, double rayDirY, double posX, double posY, int hit, int side) {

        // On va utiliser le théorème de Thalès et le théorème de pythagore pour calculer la taille du mur en pente (oui)
        // Se référer au dessin Slope_wall.png pour comprendre les variables
        double AB;
        double BE = 0;
        double AE;
        double AC;
        HashMap<String, Number> retour = new HashMap<>();
        double newPosX = posX;
        double newPosY = posY;


        if (side == 0) {
            if (rayDirY < 0) {
                AB = posY % 1;
            } else {
                AB = 1 - posY % 1;
            }
            double distX = getDist(rayDirX, newPosX);
            double distY = getDist(rayDirY, newPosY);
            BE += distY;
            while (distX <= distY) {
                if (rayDirX > 0) {
                    newPosX = newPosX + (1 - newPosX % 1);
                } else {
                    newPosX = newPosX - (newPosX % 1);
                }
                newPosY = newPosY + rayDirY * distX;
                distX = getDist(rayDirX, newPosX);
                distY = getDist(rayDirY, newPosY);
                BE += distY;
            }
            AE = Math.sqrt(AB * AB + BE * BE); // Théorème de pythagore TMTC
        } else {
            if (rayDirX < 0) {
                AB = posX % 1;
            } else {
                AB = 1 - posX % 1;
            }
            double distX = getDist(rayDirX, newPosX);
            double distY = getDist(rayDirY, newPosY);
            BE += distX;
            while (distY < distX) {
                if (rayDirY > 0) {
                    newPosY = newPosY + (1 - newPosY % 1);
                } else {
                    newPosY = newPosY - (newPosY % 1);
                }
                newPosX = newPosX + distY * rayDirX;
                distX = getDist(rayDirX, newPosX);
                distY = getDist(rayDirY, newPosY);
                BE += distX;
            }
            AE = Math.sqrt(AB * AB + BE * BE); // Théorème de pythagore TMTC
        }

        double thalesCoef = AB / (AB + BE);
        AC = AE * thalesCoef;
        if (rayDirY < 0 && rayDirX > 0 || rayDirX < 0 && rayDirY > 0) {
            AC = 1 - AC;
        }

        if (hit == 10) AC = 1 - AC;


        retour.put("distance", AC);
        retour.put("newPosX", newPosX);
        retour.put("newPosY", newPosY);

        return retour;
    }
}
