/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;



import java.util.HashMap;
import java.util.Observable;


public class Plateau extends Observable implements Runnable {

    public static final int SIZE_X = 16;
    public static final int SIZE_Y = 16;


    private HashMap<Case, Point> map = new HashMap<Case, Point>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées

    public Plateau() {
        initPlateauVide();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    public Case getCase(Case source, Direction d) {
        
        Point p = map.get(source);
        return caseALaPosition(new Point(p.x+d.dx, p.y+d.dy));


    }

    private void initPlateauVide() {

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this);
                map.put(grilleCases[x][y], new Point(x, y));
            }

        }

    }

    public void setMachine(int x, int y, Machine m) {
        grilleCases[x][y].setMachine(m);
        setChanged();
        notifyObservers();
    }


    /**
     * Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    private Case caseALaPosition(Point p) {
        Case retour = null;

        if (contenuDansGrille(p)) {
            retour = grilleCases[p.x][p.y];
        }
        return retour;
    }


    @Override
    public void run() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case c = grilleCases[x][y];
                if (c.getMachine() != null) {
                    c.getMachine().run();
                }
            }
        }
        setChanged();
        notifyObservers();
    }
}