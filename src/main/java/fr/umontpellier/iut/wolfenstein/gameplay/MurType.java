package fr.umontpellier.iut.wolfenstein.gameplay;

import javafx.scene.image.Image;

/**
 * Cette classe prend en charge l'implantation de murs sur les cartes.
 * Nous avons opté pour une énumération pour son côté pratique: elle nous permet, à la fois, de définir des types de murs récurrents,
 * mais aussi d'en définir des nouveaux de manière aisée.
 * Elle contient des méthodes d'obtention des murs, de leur texture... ETC.
 */


public enum MurType {
    PIERRE1(1), //RED
    PIERRE2(2), //GREEN
    PIERREH(3), //BLUE
    BOIS(4), //WHITE
    PIERREB(5), //YELLOW
    PORTEM(6), //PURPLE
    UNKNOWN(99); //PURPLE


    /** L'identifiant du mur est un entier. C'est utile pour la génération de la minimap et de la map elle-même. */
    private final int id;

    /**
    Les fichiers image de la texture du mur. tex correspond à la texture exposée à la lumière, tandis que shadeTex correspond à la texture foncée.
    On part du principe que les fichiers image se trouvent dans le dossier resources/tex et que les images sont des PNG nommés par leur identifiant
    suivis de la lettre A pour la texture illuminée, B pour la texture ombragée. Cette assignation a lieu dans le constructeur.
    */
    private final Image tex;
    private final Image shadeTex;

    MurType(int id){
        this.id = id;                                                  //Attribution de l'ID désiré
        int texId = this.id;                                           //Duplication de l'ID, cela va nous permettre de gagner des opérations
        this.tex = new Image("tex/"+texId+"A.png");                 //Ouverture des fichiers grâce à la classe Image de JavaFX
        this.shadeTex = new Image("tex/"+texId+"B.png");
    }

    /**
     * Getter de fichier texture, prend en charge les deux types.
     * @param side Le côté du mur qui a été touché, détermine la texture à renvoyer
     * @return L'objet Image qui contient la texture du mur
    */
    public Image getText(int side) {
        if (side == 1 || side == 3) {
            return shadeTex;
        }
        else return tex;
    }

    /**
     * Getter de type de mur. Permet de récupérer la texture à l'aide de MurType.getText(int side)
     * @param id L'identifiant de mur qu'on recherche
     * @return L'objet MurType qui correspond à l'ID spécifié
     */
    public static MurType getById(int id) {
        for(MurType e: values()){
            if(e.id == id){
                return e;
            }
        }
        return UNKNOWN;
    }
}
