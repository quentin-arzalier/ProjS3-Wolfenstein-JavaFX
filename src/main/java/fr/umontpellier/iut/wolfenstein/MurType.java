package fr.umontpellier.iut.wolfenstein;

import javafx.scene.image.Image;

import java.net.URL;


public enum MurType {
    PIERRE1(1), //RED
    PIERRE2(2), //GREEN
    PIERREH(3), //BLUE
    BOIS(4), //WHITE
    PIERREB(5), //YELLOW
    PORTEM(6); //PURPLE

    private String id;
    private Image tex;
    private Image shadtex;

    MurType(Integer id){
        this.id = id.toString();
        this.tex = new Image("tex/"+this.id+"A.png",64,64,false,false);
        this.shadtex = new Image("tex/"+this.id+"B.png",64,64,false,false);
    }

    public Image getTexs(int side) {
        switch(side){
            case 1:
                return shadtex;
            default:
                return tex;
        }
    }

    public static MurType getById(Integer id) {
        for(MurType e: values()){
            if(e.id.equals(id.toString())){
                return e;
            }
        }
        return null;
    }
}
