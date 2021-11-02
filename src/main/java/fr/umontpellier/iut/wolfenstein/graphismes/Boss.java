package fr.umontpellier.iut.wolfenstein.graphismes;

public class Boss extends Enemy{

    public Boss(float posX, float posY, String tex){
        super(posX, posY, tex);
    }

    @Override
    public String Direction(float playerX, float playerY) {
        return "0";
    }
}
