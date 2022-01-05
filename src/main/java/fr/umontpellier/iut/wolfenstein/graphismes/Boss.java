package fr.umontpellier.iut.wolfenstein.graphismes;

/**
 * Un boss n'est autre qu'un ennemi qui ne change jamais de direction de sprite par rapport au joueur.
 * La classe étend Enemy en conséquence, et override la méthode Direction pour toujours renvoyer 0
 */


public class Boss extends Enemy{

    public Boss(float posX, float posY, String tex){
        super(posX, posY, tex);
    }

    @Override
    public String direction(float playerX, float playerY) {
        return "0";
    }
}