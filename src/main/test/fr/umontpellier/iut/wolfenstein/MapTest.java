package fr.umontpellier.iut.wolfenstein;

import fr.umontpellier.iut.wolfenstein.gameplay.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    @BeforeEach
    void setUp(){
        TestApp.main(new String[0]);
    }

    @Test
    void getWorldMap_Rouge_Test() {
        // La couleur des murs rouge fait réference au chiffre 1 pour nous
        // TEST que la méthode getWorldMap retourne bien un tableau de tableau
        // de chiffre 1 représentant l'image complétement rouge (de taille 10px par 10px), rouge.png
        Map map1 = new Map("test/rouge.png");
        assertEquals(10,map1.getWidth());
        assertEquals(10,map1.getHeight());

        int [][]test = map1.getWorldMap();
        assertTrue(Arrays.stream(test).flatMapToInt(Arrays::stream).allMatch(p -> p == 1));

    }
    @Test
    void getWorldMap_vert_Test() {
        // La couleur des murs vert fait réference au chiffre 2 pour nous
        // TEST que la méthode getWorldMap retourne bien un tableau de tableau
        // de chiffre 2 représentant l'image complétement vert (de taille 10px par 10px), rouge.png
        Map map1 = new Map("test/vert.png");
        assertEquals(10,map1.getWidth());
        assertEquals(10,map1.getHeight());

        int [][]test = map1.getWorldMap();


        assertTrue(Arrays.stream(test).flatMapToInt(Arrays::stream).allMatch(p -> p == 2));

    }
}