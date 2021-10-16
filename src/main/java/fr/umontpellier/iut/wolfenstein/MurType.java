package fr.umontpellier.iut.wolfenstein;

import javafx.scene.image.Image;


public enum MurType {
    PIERRE1(1), //RED
    PIERRE2(2), //GREEN
    PIERREH(3), //BLUE
    BOIS(4), //WHITE
    PIERREB(5), //YELLOW
    PORTEM(6), //PURPLE
    GLASS(7), //PURPLE
    UNKNOWN(0); //PURPLE

    private final int id;
    private final Image tex;
    private final Image shadeTex;

    MurType(int id){
        this.id = id;
        this.tex = new Image("tex/"+this.id+"A.png");
        this.shadeTex = new Image("tex/"+this.id+"B.png");
    }

    public Image getText(int side) {
        if (side == 1) {
            return shadeTex;
        }
        return tex;
    }

    public static MurType getById(int id) {
        for(MurType e: values()){
            if(e.id == id){
                return e;
            }
        }
        return UNKNOWN;
    }
}
