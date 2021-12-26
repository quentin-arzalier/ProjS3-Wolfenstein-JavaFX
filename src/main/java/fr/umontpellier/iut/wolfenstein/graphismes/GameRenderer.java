package fr.umontpellier.iut.wolfenstein.graphismes;

import fr.umontpellier.iut.wolfenstein.gameplay.MainPlayer;
import fr.umontpellier.iut.wolfenstein.gameplay.MurType;
import fr.umontpellier.iut.wolfenstein.gameplay.Map;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public class GameRenderer extends Pane {

    private static GameRenderer instance;

    private int[][] worldMap;
    private final double[] zBuffer;
    private ArrayList<Sprite> sprites;


    private final WritableImage monImage;
    private final GraphicsContext context;
    private final MainPlayer currPlayer;
    private final Minimap minimap;


    private final int texSize = 64;
    private final int scaleFactor = 2;
    private final int screenWidth = 480;
    private final int screenHeight = 300;


    private final RenderAnimationTimer mainLoop;

    public static GameRenderer getInstance(){
        if (instance == null){
            instance = new GameRenderer();
        }
        return instance;
    }

    private GameRenderer(){
        zBuffer = new double[screenWidth];
        Canvas base = new Canvas(screenWidth *scaleFactor, screenHeight *scaleFactor);
        currPlayer = MainPlayer.getInstance();
        minimap = Minimap.getInstance();
        context = base.getGraphicsContext2D();
        this.getChildren().add(base);
        context.scale(scaleFactor, scaleFactor);
        context.setImageSmoothing(false);
        monImage = new WritableImage(screenWidth, screenHeight);
        mainLoop = new RenderAnimationTimer();
        mainLoop.start();
    }

    public void setMap(Map map){
        mainLoop.stop();
        worldMap = map.getWorldMap();
        sprites = map.getSprites();
        currPlayer.setWorldMap(map.getWorldMap());
        mainLoop.start();
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

    private class  RenderAnimationTimer extends AnimationTimer {
        /**
         * Temps écoulé au moment de l'éxecution du dernier cycle de handle en secondes
         */
        private float lastUpdate = 0;

        @Override
        public void handle(long now) {
            float nowSecondes = now / 1_000_000_000f;
            float deltaTime = (nowSecondes-lastUpdate);

            // Le premier deltaTime est toujours énorme pour une certaine raison
            if (deltaTime > 50){
                deltaTime = 0;
            }

            update(deltaTime);

            draw();
            // On actualise la variable qui stocke le moment d'exécution de l'ancienne boucle
            lastUpdate = nowSecondes;
        }
    }

    private void update(float deltaTime){
        currPlayer.update(deltaTime);
        minimap.update(deltaTime);
        // On actualise le temps local de chaque sprite pour permettre l'animation de ces derniers
        for (Sprite sprite : sprites){
            sprite.update(deltaTime);
        }
    }

    private void draw(){

        // On démarre l'algorithme de dessin du sol/plafond
        drawFloor();

        // On démarre l'algorithme de dessin des murs
        drawWalls();

        // On démarre l'algorithme de dessin des sprites
        drawSprites();

        // On dessine le buffer des pixels à l'écran en une seule fois (réduit le lag)
        context.drawImage(monImage, 0, 0);
    }
    private void drawFloor(){

        // Informations joueur
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();
        float camPitch = currPlayer.getCamPitch();


        // La position de l'horizon
        float horizon = screenHeight /2f;


        // Le vecteur caméra le plus à gauche (x = 0)
        float camVectXG = vx + vy;
        float camVectYG = vy - vx;

        // Le vecteur caméra le plus à droite (x = realWidth)
        float camVectXD = vx - vy;
        float camVectYD = vy + vx;

        // On dessine ligne par ligne donc on itère sur l'axe Y
        for (int y = 0; y < screenHeight; y++) {
            boolean isFloor = y > horizon + camPitch;

            // La position de la ligne que l'on dessine en fonction de la ligne d'horizon
            int scanLineY = y - (int)horizon - (int)camPitch;
            if (!isFloor) scanLineY *= -1; // Nécessaire pour que le plafond soit dessiné correctement lorsque le joueur bouge

            float rowDist = horizon / scanLineY;

            // Pas permettant de capter la position de la texture du mur/du sol
            float pasX = rowDist * (camVectXD - camVectXG)/ screenWidth;
            float pasY = rowDist * (camVectYD - camVectYG)/ screenWidth;

            // Les coordonnées réelles de la première colonne :
            float solX = posX + rowDist * camVectXG;
            float solY = posY + rowDist * camVectYG;

            // On commence le dessin ligne par ligne
            for (int x = 0; x < screenWidth; x++) {
                int texX = (int)(texSize * (solX % 1)) & (texSize -1);
                int texY = (int)(texSize * (solY % 1)) & (texSize -1);

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

        for (int x = 0; x < screenWidth; x++) {
            float camX = 2 * x / (float) screenWidth -1;
            float rayDirX = vx - vy * camX;
            float rayDirY = vy + vx * camX;


            DDAInfo ddaInfo = startDDA(rayDirX, rayDirY, posX, posY);

            double wallDist = ddaInfo.getWallDist();
            // On récupère toutes les informations importantes sur le mur que l'on veut dessiner
            double newPosX = ddaInfo.getNewPosX();
            double newPosY = ddaInfo.getNewPosY();
            int wallHeight =  ddaInfo.getWallHeight();
            int hit = ddaInfo.getHit();
            int side = ddaInfo.getSide();

            // Le mur d'ID 7 n'est dessiné que des côtés nord et sud et le 8 des côtés ouest et est.
            // C'est une tentative de faire des murs fins et ça rend presque bien!
            if (hit == 7 && side == 0 || hit == 8 && side == 1){
                continue;
            }


            // On détermine quel pixel de la texture doit être dessiné en premier (Sur quelle fraction du mur notre rayon a tapé)
            float wallTextY = 0;
            double pixelPos = (side == 1) ? newPosX : newPosY;
            int wallTextX = (int) ((pixelPos%1) * texSize);


            //if (side == 0 && rayDirX > 0) wallTextX = texSize - wallTextX - 1;
            //if (side == 1 && rayDirY < 1) wallTextX = texSize - wallTextX - 1;

            // On calcule le pixel de hauteur ou commencer à dessiner le mur.
            int debutDessin = -wallHeight / 2 + screenHeight / 2 + (int)(camPitch);

            // S'il est inférieur à 0, on ne commence pas la lecture du fichier texture tout en haut
            if (debutDessin < 0){
                wallTextY = -debutDessin/(float)wallHeight*texSize;
                debutDessin = 0;
            }

            // On calcule aussi la fin du dessin et on vérifie les mêmes choses.
            int finDessin = wallHeight / 2 + screenHeight / 2 + (int)(camPitch);
            if (finDessin >= screenHeight) finDessin = screenHeight - 1;

            for (int y = debutDessin; y < finDessin; y++) {
                Color color = chooseColor(hit, side, wallTextX, (int)wallTextY);
                changePixel(x, y, color);
                wallTextY += texSize /(double) wallHeight;
            }
            // On enregistre la position du mur solide touché pour savoir si on dessine ou non les différents sprites.
            zBuffer[x] = wallDist;
        }
    }


    private void drawSprites(){
        // Coordonnées du point P associées à la position du joueur (Player)
        float posX = currPlayer.getPosX();
        float posY = currPlayer.getPosY();

        // Coordonnées du vecteur vision du joueur
        float vx = currPlayer.getVx();
        float vy = currPlayer.getVy();

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

            float d = (-vy * vy - vx * vx);

            // On utilise le déterminant pour inverser correctement la matrice et replacer le sprite dans le nouveau plan
            float spriteScreenPosX = (-vy*vectorY - vx*vectorX)/d; // valeur forcément positive
            float spriteScreenPosY = (vy*vectorX - vx*vectorY)/d;

            // Il faut désormais replacer ces valeurs sur un X de coordonnées realWidth et non mapSize
            int screenPosX  = (int) ((screenWidth / 2) * (1 + spriteScreenPosY / spriteScreenPosX));
            // La taille en pixels du sprite sur l'écran.
            int tailleSprite = (int) Math.abs(screenHeight / spriteScreenPosX);

            int demiSprite = tailleSprite/2;

            int gaucheSprite = screenPosX - demiSprite;
            if (gaucheSprite < 0) gaucheSprite = 0;
            int droiteSprite = screenPosX + demiSprite;
            if (droiteSprite >= screenWidth) droiteSprite = screenWidth - 1;

            int camPitch = (int)currPlayer.getCamPitch();
            int hautSprite = screenHeight /2 - demiSprite + camPitch;
            if (hautSprite < 0) hautSprite = 0;
            int basSprite = screenHeight /2 + demiSprite + camPitch;
            if (basSprite >= screenHeight) basSprite = screenHeight - 1 ;

            // On dessine le sprite
            if (spriteScreenPosX > 0.5){ // On ne dessine le sprite que si il se trouve au moins 0.5 unités devant le joueur
                for (int x = gaucheSprite; x < droiteSprite; x++) {

                    int texX = (x + tailleSprite/2 - screenPosX) * texSize / tailleSprite;

                    if (spriteScreenPosX < zBuffer[x]){ // On vérifie si la colonne à dessiner se trouve bien devant un mur
                        for (int y = hautSprite; y < basSprite; y++) {

                            int texY = (y - camPitch - screenHeight /2 + tailleSprite/2) * texSize / tailleSprite;

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
     * Cette méthode permet de savoir de quel couleur est le pixel à dessiner du mur en question
     * @param hit L'identifiant du mur dans la matrice worldMap
     * @param side Le côté touché permet de déterminer si l'on doit prendre la texture plus foncée ou non.
     * @param texX La position X du pixel à lire sur le fichier texture
     * @param texY La position Y du pixel à lire sur le fichier texture
     * @return La couleur du pixel à dessiner
     */

    public Color chooseColor(int hit,int side,int texX,int texY){
        return MurType.getById(hit).getText(side).getPixelReader().getColor(texX,texY);
    }

    /**
     * L'algorithme permettant de calculer la distance d'un mur par rapport au joueur
     * (DDA signifie Digital Differential Analyzer, ADN ou analyseur différentiel numérique en français)
     * @param rayDirX Les coordonnées X du vecteur vision actuel
     * @param rayDirY Les coordonnées Y du vecteur vision actuel
     * @param posX Les coordonnées X du joueur
     * @param posY Les coordonnées Y du joueur
     * @return Une Map contenant toutes les infos nécessaires au remplissage de l'image, identifiées par leur nom
     */
    public DDAInfo startDDA(float rayDirX, float rayDirY, float posX, float posY){
        double distX = getDist(rayDirX, posX);
        double distY = getDist(rayDirY, posY);
        int xi = (int) posX;
        int yi = (int) posY;


        double newPosX = posX;
        double newPosY = posY;
        float wallDist = 0;
        int hit = 0;
        int side = 0;

        // Algorithme de détection des murs
        while (hit == 0) {
            if (distX <= distY) {
                wallDist += distX;
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
                wallDist += distY;
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
        }

        return new DDAInfo(newPosX, newPosY, wallDist, (int) (screenHeight / wallDist), hit, side);
    }

    /**
     * Cette methode calcule la distance du joueur par rapport au mur en fonction de v sur un axe en particulier
     * @param rayDir Il s'agit de la direction du rayon de vision sur l'axe donné
     * @param pos Il s'agit de la position du joueur sur l'axe donné
     * @return La distance normalisée du prochain mur
     */
    public double getDist(double rayDir, double pos){
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
        }
        return dist;
    }
}
