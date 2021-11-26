package fr.umontpellier.iut.wolfenstein.gameplay;

import fr.umontpellier.iut.wolfenstein.reseau.WolfClient;
import javafx.scene.input.KeyCode;

import java.util.HashSet;

public class MainPlayer extends Player{

    /**
     * La carte dans laquelle se déplace le joueur
     */
    private int[][] worldMap;

    /**
     * L'objet Player contrôlé par le joueur
     */
    private static MainPlayer instance;

    /**
     * La rotation de la caméra vers le haut/le bas
     */
    private float camPitch = 0;

    /**
     * La quantité de déplacement du joueur en cases chaque seconde
     */
    private final float moveAmount = 5;

    /**
     * La vitesse de rotation du joueur (utilisée pour la rotation à la souris)
     */
    private float rotAmount = 3;

    /**
     * La liste contenant toutes les touches enfoncées par l'utilisateur
     */
    private HashSet<KeyCode> pressedKeys;


    private MainPlayer(int[][] worldMap) {
        super();
        this.worldMap = worldMap;
        this.pressedKeys = new HashSet<>();
    }

    public void setWorldMap(int[][] worldMap) {
        this.worldMap = worldMap;
    }

    public static MainPlayer getInstance(){
        if (instance == null){
            instance = new MainPlayer(new Map("levels/level0.png").getWorldMap());
        }
        return instance;
    }

    public void update(double deltaTime){
        moveCharacter((float)deltaTime);
        if (getMultiplayer()) WolfClient.getInstance().sendCommand(getPosAsString()); // Utilisée uniquement en cas de multijoueur pour partager sa position aux autres
    }

    private void moveCharacter(float deltaTime) {
        float vx = getVx();
        float vy = getVy();
        if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.Z)){
            move(vx * moveAmount * deltaTime, vy * moveAmount * deltaTime);
        }
        if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)){
            move(-vx * moveAmount * deltaTime, -vy * moveAmount * deltaTime);
        }
        if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.Q)){
            move(vy * moveAmount * deltaTime, -vx * moveAmount * deltaTime);
        }
        if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)){
            move(-vy * moveAmount * deltaTime, vx * moveAmount * deltaTime);
        }
    }

    private void move(float movX, float movY){
        float posX = getPosX();
        float posY = getPosY();
        if (isPositionValid(posX + movX, posY)){
            setPosX(posX + movX);
            getSprite().updatePos(posX + movX, posY);
        }
        if (isPositionValid(posX, posY + movY)){
            setPosY(posY + movY);
            getSprite().updatePos(posX, posY + movY);
        }
    }

    private boolean isPositionValid(float newPosX, float newPosY) {
        int xi = (int) newPosX;
        int yi = (int) newPosY;
        if (worldMap[xi][yi] != 0) return false;
        if (newPosX%1 >= 0.9 && worldMap[xi+1][yi] != 0) return false;
        if (newPosX%1 <= 0.1 && worldMap[xi-1][yi] != 0) return false;
        if (newPosY%1 >= 0.9 && worldMap[xi][yi+1] != 0) return false;
        if (newPosY%1 <= 0.1 && worldMap[xi][yi-1] != 0) return false;
        return true;
    }


    /**
     * Cette méthode est utilisée pour changer l'angle de la caméra verticalement (si le joueur regarde vers le haut ou vers le bas)
     * @param offset La différence à ajouter à la variable (dépend du mouvement de la souris du joueur lors de l'appel)
     */
    public void moveCameraPitch(float offset) {
        this.camPitch += offset;
        if (camPitch < -200) camPitch = -200;
        if (camPitch > 200) camPitch = 200;
    }

    /**
     * Manipulation de l'angle de la caméra horizontalement.
     * @param input On utilise un input pour déterminer si l'utilisateur a beaucoup déplacé la souris ou très peu.
     */
    public void lookSide(float input, boolean isLeft){
        float oldRotSpeed = rotAmount;           // rotSpeed est une valeur qui dépend de la fréquence d'images du jeu afin d'éviter des problèmes liés au ralentissements
        rotAmount = rotAmount * Math.abs(input); // On multiplie cette vitesse par la quantité de pixels déplacées par la souris
        if (isLeft) rotAmount *= -1;

        float vx = getVx();
        float vy = getVy();
        setVx((float) (vx * Math.cos(-rotAmount) - vy * Math.sin(-rotAmount)));
        setVy((float) (vx * Math.sin(-rotAmount) + vy * Math.cos(-rotAmount)));
        rotAmount = oldRotSpeed;
    }

    /**
     * On réinitialise les variables du joueur aux valeurs par défaut (lors du changement de carte par exemple)
     */
    public void resetPos() {
        setPosX(16.5f);
        setPosY(16.5f);
        setVx(0);
        setVy(1);
    }

    public void setPressedKeys(HashSet<KeyCode> pressedKeys) {
        this.pressedKeys = pressedKeys;
    }

    public float getCamPitch() {
        return camPitch;
    }

    /**
     * Méthode utilisée en cas de multijoueur pour partager sa position aux autres
     */
    public String getPosAsString(){
        return "PLAYERPOS" + getPlayerID() + ":" + getPosX() + ", " + getPosY() + ", " + getVx() + ", " + getVy();
    }
}
