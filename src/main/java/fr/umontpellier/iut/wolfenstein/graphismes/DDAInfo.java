package fr.umontpellier.iut.wolfenstein.graphismes;

/**
 * DDAInfo est la classe qui stocke les données retournées par l'analyseur différentiel numérique
 * permettant d'obtenir des informations sur la position du mur et bien plus encore.
 */
public class DDAInfo {

    /**
     * newPosX est la position sur l'axe X de notre vecteur à la fin du calcul (donc la position sur l'axe X du bord du mur touché)
     */
    private final double newPosX;

    /**
     * newPosY est la position sur l'axe Y de notre vecteur à la fin du calcul (donc la position sur l'axe Y du bord du mur touché)
     */
    private final double newPosY;

    /**
     * wallDist est la distance à laquelle se trouve le mur par rapport au joueur
     */
    private final double wallDist;

    /**
     * wallHeight est la taille en pixel du mur à dessiner à l'écran
     */
    private final int wallHeight;

    /**
     * hit est le type de mur touché. Pour plus d'informations sur les types de murs, voir MurType
     */
    private final int hit;

    /**
     * side est le côté du mur touché (varie entre 0 et 3. 0 pour le nord et le reste dans le sens des aiguilles d'une montre)
     */
    private final int side;

    public DDAInfo(double newPosX, double newPosY, double wallDist, int wallHeight, int hit, int side) {
        this.newPosX = newPosX;
        this.newPosY = newPosY;
        this.wallDist = wallDist;
        this.wallHeight = wallHeight;
        this.hit = hit;
        this.side = side;
    }

    public double getNewPosX() {
        return newPosX;
    }

    public double getNewPosY() {
        return newPosY;
    }

    public double getWallDist() {
        return wallDist;
    }

    public int getWallHeight() {
        return wallHeight;
    }

    public int getHit() {
        return hit;
    }

    public int getSide() {
        return side;
    }
}
