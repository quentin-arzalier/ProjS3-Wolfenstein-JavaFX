package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.graphismes.Sprite;
import fr.umontpellier.iut.wolfenstein.reseau.WolfClient;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Set;

public class Player {

    // Les informations du joueur pour le multijoueur
    private final Color color;
    private final Sprite sprite;

    // La position du joueur dans le quadrillage
    private float posX = 16.5f;
    private float posY = 16.5f;

    // Le vecteur direction du joueur
    private float vx = 0;
    private float vy = 1;

    // La rotation de la caméra vers le haut/le bas
    private float camPitch = 0;

    // Les vitesses de déplacement du joueur
    private float moveSpeed = 0;
    private float rotSpeed = 0;

    // Rayon du joueur, pour les collisions avec les murs
    private static final float radius = .1f;
    // Vitesse angulaire du joueur lors du déplacement au clavier
    // Rmq: les valeurs du sin et cos de l'angle pourraient être codées en dur
    private static final float cosAngle = (float) Math.cos(.05);
    private static final float sinAngle = (float) Math.sin(.05);

    // Ensemble des touches enfoncées (maintenu à jour par MainApp)
    private Set<KeyCode> pressedKeys;

    // Carte sur laquelle le joueur se déplace
    private Map map;

    private final int playerID;

    private boolean isMultiplayer = false;

    // Couleurs des joueurs en fonction de leur numéro
    private static final Color[] playerColors = {Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN};

    public Player(int nb) {
        color = playerColors[nb - 1];
        sprite = new Sprite(posX, posY, "player" + nb);
        playerID = nb;
    }

    public void setMultiplayer() {
        isMultiplayer = true;
    }

    /**
     * On réinitialise les variables du joueur aux valeurs par défaut (lors du changement de carte par exemple)
     */
    public void resetPos() {
        posX = 16.5f;
        posY = 16.5f;
        vx = 0;
        vy = 1;
    }

    /**
     * Teste si une position donnée peut être occupée par le joueur
     *
     * @param x abscique de la position
     * @param y ordonnée de la position
     * @return true si le joueur peut aller à cette position, false sinon
     */
    private boolean isValidPosition(float x, float y) {
        int[][] worldMap = map.getWorldMap();
        int ix = (int) x;
        int iy = (int) y;
        float fx = x % 1;
        float fy = y % 1;
        if (worldMap[ix][iy] != 0) return false;
        if (fx < radius && worldMap[ix - 1][iy] != 0) return false;
        if (fx > 1 - radius && worldMap[ix + 1][iy] != 0) return false;
        if (fy < radius && worldMap[ix][iy - 1] != 0) return false;
        if (fy > 1 - radius && worldMap[ix][iy + 1] != 0) return false;
        return true;
    }

    /**
     * Déplace le joueur de la quantité indiquée en x et en y
     *
     * @param dx déplacement en x
     * @param dy déplacement en y
     */
    public void move(float dx, float dy) {
        if (isValidPosition(posX + dx, posY)) {
            posX += dx;
        }
        if (isValidPosition(posX, posY + dy)) {
            posY += dy;
        }
        sprite.updatePos(posX, posY);
    }

    /**
     * Déplace le joueur en fonction des touches appuyées
     */
    public void moveCharacter() {
        if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W)) {
            move(vx * moveSpeed, vy * moveSpeed);
        }
        if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)) {
            move(-vx * moveSpeed, -vy * moveSpeed);
        }
        if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A)) {
//            move(-vy * moveSpeed, vx * moveSpeed);
            float x = vx;
            float y = vy;
            vx = cosAngle * x + sinAngle * y;
            vy = -sinAngle * x + cosAngle * y;
        }
        if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)) {
//            move(vy * moveSpeed, -vx * moveSpeed);
            float x = vx;
            float y = vy;
            vx = cosAngle * x - sinAngle * y;
            vy = sinAngle * x + cosAngle * y;
        }
        if (isMultiplayer)
            WolfClient.getInstance().sendCommand(getPosAsString()); // Utilisée uniquement en cas de multijoueur pour partager sa position aux autres
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setPressedKeys(Set<KeyCode> pressedKeys) {
        this.pressedKeys = pressedKeys;
    }

    /**
     * Cette méthode est utilisée pour changer l'angle de la caméra verticalement (si le joueur regarde vers le haut ou vers le bas)
     *
     * @param offset La différence à ajouter à la variable (dépend du mouvement de la souris du joueur lors de l'appel)
     */
    public void moveCameraPitch(float offset) {
        this.camPitch += offset;
        if (camPitch < -200) camPitch = -200;
        if (camPitch > 200) camPitch = 200;
    }

    /**
     * Manipulation de l'angle de la caméra horizontalement.
     *
     * @param input On utilise un input pour déterminer si l'utilisateur a beaucoup déplacé la souris ou très peu.
     */
    public void lookLeft(float input) {
        float oldRotSpeed = rotSpeed;          // rotSpeed est une valeur qui dépend de la fréquence d'images du jeu afin d'éviter des problèmes liés au ralentissements
        rotSpeed = rotSpeed * Math.abs(input); // On multiplie cette vitesse par la quantité de pixels déplacées par la souris

        // On nous a dit qu'on aurait pas besoin des sinus et cosinus mais le tutoriel utilisait cette méthode pour bouger correctement les vecteurs caméra et latéraux
        float oldVx = vx;
        vx = (float) (vx * Math.cos(-rotSpeed) - vy * Math.sin(-rotSpeed));
        vy = (float) (oldVx * Math.sin(-rotSpeed) + vy * Math.cos(-rotSpeed));
        rotSpeed = oldRotSpeed;

        if (isMultiplayer) WolfClient.getInstance().sendCommand(getPosAsString());
    }

    /**
     * Manipulation de l'angle de la caméra horizontalement.
     *
     * @param input On utilise un input pour déterminer si l'utilisateur a beaucoup déplacé la souris ou très peu.
     */
    public void lookRight(float input) {
        float oldRotSpeed = rotSpeed;
        rotSpeed = rotSpeed * Math.abs(input);
        float oldVx = vx;
        vx = (float) (vx * Math.cos(rotSpeed) - vy * Math.sin(rotSpeed));
        vy = (float) (oldVx * Math.sin(rotSpeed) + vy * Math.cos(rotSpeed));
        rotSpeed = oldRotSpeed;

        if (isMultiplayer) WolfClient.getInstance().sendCommand(getPosAsString());
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public float getCamPitch() {
        return camPitch;
    }

    public Color getColor() {
        return color;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setRotSpeed(float rotSpeed) {
        this.rotSpeed = rotSpeed;
    }

    public Sprite getSprite() {
        return sprite;
    }


    /**
     * Les deux méthodes çi dessous ne sont utilisées que pour le multijoueur afin de positionner les autres joueurs à chaque fois que leur position change.
     */


    public String getPosAsString() {
        return "PLAYERPOS" + playerID + ":" + posX + ", " + posY + ", " + vx + ", " + vy;
    }

    public void setPosWithString(String posString) {
        String list = posString.split(":")[1];
        String[] info = list.split(", ");
        this.posX = Float.parseFloat(info[0]);
        this.posY = Float.parseFloat(info[1]);
        this.vx = Float.parseFloat(info[2]);
        this.vy = Float.parseFloat(info[3]);
        sprite.updatePos(posX, posY);
    }
}
