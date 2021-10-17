package fr.umontpellier.iut.wolfenstein;

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

    private EnemyInd e;


    private final int texSize = 64;
    private final int drawWidth = 480*2;
    private final int drawHeight = 300*2;
    private final int realWidth = 480;
    private final int realHeight = 300;


    private AnimationTimer renderer;

    public GameRenderer(Player p, Minimap map){
        zBuffer = new float[realWidth];
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

    public void addEnemy(EnemyInd e){
        this.e =e;
        sprites.add(e.getSprite());
    }

    public void setMap(Map map){
        renderer.stop();
        worldMap = map.getWorldMap();
        sprites = map.getSprites();
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

                // On démarre l'algorithme de dessin du sol/plafond
                drawFloor();

                // On démarre l'algorithme de dessin des murs
                drawWalls();

                // On démarre l'algorithme de dessin des sprites
                drawSprites();
                context.drawImage(monImage, 0, 0);

                // On calcule la vitesse de déplacement du joueur pour qu'elle soit constance même avec des variations de fps
                float frameTime = (now - lastUpdate) / 1_000_000_000f;
                currPlayer.setMoveSpeed(frameTime * 5);
                currPlayer.setRotSpeed(frameTime * 3);
                currPlayer.moveCharacter(worldMap);
                e.moveEnemy();
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

    private void drawWalls() {
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();
        float latX = currPlayer.getLatX();
        float latY = currPlayer.getLatY();
        float camPitch = currPlayer.getCamPitch();

        for (int x = 0; x < realWidth; x++) {
            float camX = 2 * x / (float) realWidth -1;
            float rayDirX = vx + latX * camX;
            float rayDirY = vy + latY * camX;

            HashMap<String, Number> ddaInfo = startDDA(rayDirX, rayDirY, posX, posY);



            int nbHits = ddaInfo.get("nbHits").intValue();
            float t = ddaInfo.get("t").floatValue();

            for (int i = nbHits-1; i >= 0; i--) {


                float newPosX = ddaInfo.get("newPosX"+i).floatValue();
                float newPosY = ddaInfo.get("newPosY"+i).floatValue();
                int wallHeight =  ddaInfo.get("wallHeight"+i).intValue();
                int hit = ddaInfo.get("hit"+i).intValue();
                int side = ddaInfo.get("side"+i).intValue();

                if (hit == 7 && side == 0 || hit == 8 && side == 1){
                    continue;
                }


                int wallTextX;
                float wallTextY = 0;
                float pixelPos = (side == 1) ? newPosX : newPosY;

                wallTextX = (int) ((pixelPos%1) * texSize);
                //if (side == 0 && rayDirX > 0) wallTextX = texSize - wallTextX - 1;
                //if (side == 1 && rayDirY < 1) wallTextX = texSize - wallTextX - 1;

                if(wallHeight >= realHeight) wallTextY += (wallHeight - realHeight) /2f/(float)wallHeight * texSize;


                int finToit = -wallHeight / 2 + realHeight / 2 + (int)(camPitch);
                if (finToit < 0){
                    //float maxSize = Math.max(wallHeight, realHeight);
                    //float camOffset = (-finToit)/ maxSize *texSize;
                    finToit = 0;
                    //wallTextY += camOffset;
                }

                int debutSol = wallHeight / 2 + realHeight / 2 + (int)(camPitch);
                if (debutSol >= realHeight) debutSol = realHeight - 1;

                for (int y = finToit; y < debutSol; y++) {
                    Color color = chooseColor(hit, side, wallTextX, (int)wallTextY);
                    if (nbHits > 1 && i < nbHits-1){
                        Color oldColor = monImage.getPixelReader().getColor(x, y);
                        color = new Color((color.getRed()+oldColor.getRed())/2, (color.getGreen()+oldColor.getGreen())/2, (color.getBlue()+oldColor.getBlue())/2, 1);
                    }
                    changePixel(x, y, color);
                    wallTextY += texSize /(double) wallHeight;
                }
            }
            zBuffer[x] = t;
        }
    }

    private void drawFloor(){

        // Informations joueur
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();
        float latX = currPlayer.getLatX();
        float latY = currPlayer.getLatY();
        float camPitch = currPlayer.getCamPitch();


        // La position de l'horizon
        float horizon = realHeight/2f;


        // Le vecteur caméra le plus à gauche (x = 0)
        float camVectXG = vx - latX;
        float camVectYG = vy - latY;

        // Le vecteur caméra le plus à droite (x = realWidth)
        float camVectXD = vx + latX;
        float camVectYD = vy + latY;

        for (int y = 0; y < realHeight; y++) {
            boolean isFloor = y > horizon + camPitch;

            // La position de la ligne que l'on dessine en fonction de la ligne d'horizon
            int scanLineY = y - (int)horizon - (int)camPitch;
            if (!   isFloor) scanLineY *= -1;

            float rowDist = horizon / scanLineY;

            // Pas permettant de capter la position de la texture du mur/du sol
            float pasX = rowDist * (camVectXD - camVectXG)/realWidth;
            float pasY = rowDist * (camVectYD - camVectYG)/realWidth;

            // Les coordonnées réelles de la première colonne :
            float solX = posX + rowDist * camVectXG;
            float solY = posY + rowDist * camVectYG;

            // On commence le dessin ligne par ligne
            for (int x = 0; x < realWidth; x++) {
                int texX = (int)(texSize * (solX % 1)) & (texSize -1);
                int texY = (int)(texSize * (solY % 1)) & (texSize -1);

                solX += pasX;
                solY += pasY;
                int text = 4;
                if (isFloor) text = 2;

                Color color = chooseColor(text, 1, texX, texY);

                changePixel(x, y, color);
                
            }
        }


    }

    private void drawSprites(){
        // Coordonnées du point P associées à la position du joueur (Player)
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();

        // Coordonnées du vecteur vision du joueur
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();

        // Coordonnées du vecteur caméra du joueur (orthogonal au vecteur vision)
        float latX = currPlayer.getLatX();
        float latY = currPlayer.getLatY();

        // On trie les sprites dans l'ordre décroissant des distances au joueur pour afficher les plus proches en dernier (au premier plan)
        for (Sprite s : sprites) {
            s.setDist(posX, posY);
        }
        Collections.sort(sprites);

        for (Sprite currSprite : sprites) {
            // Coordonnées du point S associées à la position du sprite
            float spritePosX = currSprite.getPosX();
            float spritePosY = currSprite.getPosY();

            // Coordonnées du vecteur entre les points P et S (PS)
            // Il s'agit également des coordonnées du sprite dans le plan centré en (posX, posY)
            float vectorX = spritePosX - posX;
            float vectorY = spritePosY - posY;

            /* ON VA SE SERVIR DU SPRINT 2 DE MOD MATHS WOOOOOOOOOOO
             *
             *                                                          ( latX vx )
             * Matrice M du plan d'axes vecteur vision/vecteur caméra : ( latY vy )
             *
             * On a donc le système suivant :
             * { a*latX + b*vx = vectorX
             * { a*latY + b*vy = vectorY
             * Que l'on transforme en ce calcul matriciel :
             * (latX vx)(a) = (vectorX)
             * (latY vy)(b) = (vectorY)
             * avec a => la position du sprite latéralement sur l'écran (sur l'axe X, celui du vecteur caméra)
             * et   b => la profondeur du sprite dans l'écran           (sur l'axe Y, celui du vecteur vision)
             *
             * Donnons d le déterminant de la matrice M = latX * vy - vx * latY
             * Inversons M pour obtenir facilement les valeurs de a et de b :
             * (vy/d    -vx/d ) (vectorX) = (a)
             * (-latY/d latX/d) (vectorY) = (b)
             * On obtient donc le système que l'on peut résoudre tel que voilà :
             * { (vy*vectorX - vx*vectorY)/d = a
             * { (latX*vectorY - latY*vectorX)/d = b
             * Calculons le déterminant de la matrice M du plan pour trouver les coordonnées du sprite dans ce dernier.
             */

            float d = (latX * vy - vx * latY);

            // On utilise le déterminant pour inverser correctement la matrice et replacer le sprite dans le nouveau plan
            float spriteScreenPosX = (latX*vectorY - latY*vectorX)/d; // valeur forcément positive
            float spriteScreenPosY = (vy*vectorX - vx*vectorY)/d;

            // Il faut désormais replacer ces valeurs sur un X de coordonnées realWidth et non mapSize
            int screenPosX  = (int) ((realWidth / 2) * (1 + spriteScreenPosY / spriteScreenPosX));
            // La taille en pixels du sprite sur l'écran.
            int tailleSprite = (int) Math.abs(realHeight / spriteScreenPosX);

            int demiSprite = tailleSprite/2;

            int gaucheSprite = screenPosX - demiSprite;
            if (gaucheSprite < 0) gaucheSprite = 0;
            int droiteSprite = screenPosX + demiSprite;
            if (droiteSprite >= realWidth) droiteSprite = realWidth - 1;

            int camPitch = (int)currPlayer.getCamPitch();
            int hautSprite = realHeight/2 - demiSprite + camPitch;
            if (hautSprite < 0) hautSprite = 0;
            int basSprite = realHeight/2 + demiSprite + camPitch;
            if (basSprite >= realHeight) basSprite = realHeight - 1 ;

            // On dessine le sprite
            if (spriteScreenPosX > 0.5){ // On ne dessine le sprite que si il se trouve au moins 0.5 unités devant le joueur
                for (int x = gaucheSprite; x < droiteSprite; x++) {

                    int texX = (x + tailleSprite/2 - screenPosX) * texSize / tailleSprite;

                    if (spriteScreenPosX < zBuffer[x]){ // On vérifie si la colonne à dessiner se trouve bien devant un mur
                        for (int y = hautSprite; y < basSprite; y++) {

                            int texY = (y - camPitch - realHeight/2 + tailleSprite/2) * texSize / tailleSprite;

                            Color color = currSprite.getTex().getPixelReader().getColor(texX, texY);
                            if (color.getOpacity() != 0){
                                changePixel(x, y, color);
                            }

                        }
                    }
                }
            }
        }

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
        int side;
        double t = 0;
        HashMap<String, Number> retour = new HashMap<>();
        int nbHits = 0;

        // Algorithme de détection des murs
        while (hit == 0) {
            if (distX <= distY) {
                t += distX;
                if (rayDirX > 0){
                    xi++;
                    newPosX = newPosX + (1 - newPosX%1);
                }
                else {
                    xi--;
                    newPosX = newPosX - (newPosX%1);
                }
                newPosY = newPosY + distX * rayDirY;
                side = 0;
            }
            else {
                t += distY;
                if (rayDirY > 0){
                    yi++;
                    newPosY = newPosY + (1 - newPosY%1);
                }
                else {
                    yi--;
                    newPosY = newPosY - (newPosY%1);
                }
                newPosX = newPosX + distY * rayDirX;
                side = 1;
            }
            distX = getDist(rayDirX, newPosX);
            distY = getDist(rayDirY, newPosY);

            hit = worldMap[xi][yi];

            if (hit != 0){
                double oldX = newPosX;
                double oldY = newPosY;
                if (hit == 9 || hit == 10){
                    HashMap<String, Number> slopeInfo =  getSlopeDist(rayDirX, rayDirY, newPosX, newPosY, hit, side);
                    newPosX = slopeInfo.get("newPosX").doubleValue();
                    newPosY = slopeInfo.get("newPosY").doubleValue();
                    double distance = slopeInfo.get("distance").doubleValue();
                    t += distance;
                }
                retour.put("hit"+nbHits, hit);
                retour.put("side"+nbHits, side);
                Number wallHeight = realHeight / t;
                retour.put("wallHeight"+nbHits, wallHeight);
                retour.put("newPosX"+nbHits, newPosX);
                retour.put("newPosY"+nbHits, newPosY);
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
            if (rayDirY < 0){
                AB = posY % 1;
            }
            else  {
                AB = 1 - posY % 1;
            }
            double distX = getDist(rayDirX, newPosX);
            double distY = getDist(rayDirY, newPosY);
            BE += distY;
            while (distX <= distY){
                if (rayDirX > 0){
                    newPosX = newPosX + (1 - newPosX%1);
                }
                else {
                    newPosX = newPosX - (newPosX%1);
                }
                newPosY = newPosY + rayDirY * distX;
                distX = getDist(rayDirX, newPosX);
                distY = getDist(rayDirY, newPosY);
                BE += distY;
            }
            AE = Math.sqrt(AB*AB + BE*BE); // Théorème de pythagore TMTC
        }
        else {
            if (rayDirX < 0){
                AB = posX % 1;
            }
            else  {
                AB = 1 - posX % 1;
            }
            double distX = getDist(rayDirX, newPosX);
            double distY = getDist(rayDirY, newPosY);
            BE += distX;
            while (distY < distX){
                if (rayDirY > 0){
                    newPosY = newPosY + (1 - newPosY%1);
                }
                else {
                    newPosY = newPosY - (newPosY%1);
                }
                newPosX = newPosX + distY * rayDirX;
                distX = getDist(rayDirX, newPosX);
                distY = getDist(rayDirY, newPosY);
                BE += distX;
            }
            AE = Math.sqrt(AB*AB + BE*BE); // Théorème de pythagore TMTC
        }

        double thalesCoef = AB/(AB+BE);
        AC =  AE*thalesCoef;
        if (rayDirY < 0 && rayDirX > 0 || rayDirX < 0 && rayDirY > 0){
            AC = 1 - AC;
        }

        if (hit == 10) AC = 1 - AC;


        retour.put("distance", AC);
        retour.put("newPosX", newPosX);
        retour.put("newPosY", newPosY);

        return retour;
    }
}
