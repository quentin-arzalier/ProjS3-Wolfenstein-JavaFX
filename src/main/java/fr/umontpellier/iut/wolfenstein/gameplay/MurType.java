package fr.umontpellier.iut.wolfenstein.gameplay;

import javafx.scene.image.Image;


public enum MurType {
    PIERRE1(1), //RED
    PIERRE2(2), //GREEN
    PIERREH(3), //BLUE
    BOIS(4), //WHITE
    PIERREB(5), //YELLOW
    PORTEM(6), //PURPLE
    GLASSNS(7), //Cyan clair
    GLASSEW(8), //PURPLE
    GLASSCORNERUP(9), //Cyan clair
    GLASSCORNERDOWN(10), //Cyan clair
    UNKNOWN(0); //PURPLE

    private final int id;
    private final Image tex;
    private final Image shadeTex;

    MurType(int id){
        this.id = id;
        int texId = this.id;
        if (id <= 10 && id >= 7) texId = 7;
        this.tex = new Image("tex/"+texId+"A.png");
        this.shadeTex = new Image("tex/"+texId+"B.png");
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
