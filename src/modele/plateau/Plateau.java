/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

import modele.item.Color;
import modele.item.ItemColor;
import modele.item.ItemShape;

public class Plateau extends Observable implements Runnable {

    public static final int SIZE_X = 16;
    public static final int SIZE_Y = 16;

    private HashMap<Case, Point> map = new HashMap<Case, Point>(); // permet de récupérer la position d'une case à
                                                                   // partir de sa référence
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées

    public Plateau() {
        initPlateauVide();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    public Point getPosition(Case c) {
        Point p = map.get(c);
        if (p == null) {
            return null;
        }
        return new Point(p.x, p.y);
    }

    public Case getCase(Case source, Direction d) {

        Point p = map.get(source);
        return caseALaPosition(new Point(p.x + d.dx, p.y + d.dy));

    }

    private void initPlateauVide() {

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this);
                map.put(grilleCases[x][y], new Point(x, y));
            }

        }
        // Gisements de couleurs primaires en haut de la map : 3 groupes de 3 cases.
        addColorDepositStrip(0, 0, Color.Red);
        addColorDepositStrip(4, 0, Color.Green);
        addColorDepositStrip(8, 0, Color.Blue);

        // Gisements fixes en bas de la map : 4 groupes de 3 cases.
        addDepositStrip(1, SIZE_Y - 1, ItemShape.ShapeType.CIRCLE);
        addDepositStrip(5, SIZE_Y - 1, ItemShape.ShapeType.SQUARE);
        addDepositStrip(9, SIZE_Y - 1, ItemShape.ShapeType.STAR);
        addDepositStrip(13, SIZE_Y - 1, ItemShape.ShapeType.FAN);

    }

    private void addDepositStrip(int startX, int y, ItemShape.ShapeType shapeType) {
        for (int i = 0; i < 3; i++) {
            grilleCases[startX + i][y].setGisement(new ItemShape(shapeType));
        }
    }

    private void addColorDepositStrip(int startX, int y, Color color) {
        for (int i = 0; i < 3; i++) {
            grilleCases[startX + i][y].setGisement(new ItemColor(color));
        }
    }

    public void setMachine(int x, int y, Machine m) {
        grilleCases[x][y].setMachine(m);
        setChanged();
        notifyObservers();
    }

    public boolean containsMachine(Machine machine) {
        if (machine == null) {
            return false;
        }
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                if (grilleCases[x][y].getMachine() == machine) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean rotateMachine(Machine machine) {
        if (machine == null || machine instanceof ZoneLivraison || !containsMachine(machine)) {
            return false;
        }

        Point origin = getPosition(machine.getCase());
        if (origin == null) {
            return false;
        }

        Direction nextDirection = machine.getDirection().nextClockwise();
        Point[] currentFootprint = machine.getFootprint(machine.getDirection());
        Point[] nextFootprint = machine.getFootprint(nextDirection);

        if (!canOccupy(machine, origin, nextFootprint)) {
            return false;
        }

        Set<String> currentPositions = footprintKeys(origin, currentFootprint);
        Set<String> nextPositions = footprintKeys(origin, nextFootprint);

        for (String currentPosition : currentPositions) {
            if (!nextPositions.contains(currentPosition)) {
                Point point = pointFromKey(currentPosition);
                grilleCases[point.x][point.y].clearMachine();
            }
        }

        machine.rotateClockwise();
        for (Point offset : nextFootprint) {
            int targetX = origin.x + offset.x;
            int targetY = origin.y + offset.y;
            if (grilleCases[targetX][targetY].getMachine() != machine) {
                grilleCases[targetX][targetY].setMachine(machine);
            }
        }

        setChanged();
        notifyObservers();
        return true;
    }

    public boolean removeMachine(int x, int y) {
        Machine target = grilleCases[x][y].getMachine();
        if (target == null || target instanceof ZoneLivraison) {
            return false;
        }

        target.clearItems();

        for (int xx = 0; xx < SIZE_X; xx++) {
            for (int yy = 0; yy < SIZE_Y; yy++) {
                if (grilleCases[xx][yy].getMachine() == target) {
                    grilleCases[xx][yy].clearMachine();
                }
            }
        }

        setChanged();
        notifyObservers();
        return true;
    }

    /**
     * Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    private boolean canOccupy(Machine machine, Point origin, Point[] footprint) {
        for (Point offset : footprint) {
            Point target = new Point(origin.x + offset.x, origin.y + offset.y);
            if (!contenuDansGrille(target)) {
                return false;
            }
            Machine occupant = grilleCases[target.x][target.y].getMachine();
            if (occupant != null && occupant != machine) {
                return false;
            }
        }
        return true;
    }

    private Set<String> footprintKeys(Point origin, Point[] footprint) {
        Set<String> keys = new HashSet<String>();
        for (Point offset : footprint) {
            keys.add(key(origin.x + offset.x, origin.y + offset.y));
        }
        return keys;
    }

    private String key(int x, int y) {
        return x + ":" + y;
    }

    private Point pointFromKey(String key) {
        String[] parts = key.split(":");
        return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
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
                if (c.getMachine() != null && c.getMachine().getCase() == c) {
                    c.getMachine().run();
                }
            }
        }
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case c = grilleCases[x][y];
                if (c.getMachine() != null && c.getMachine().getCase() == c) {
                    c.getMachine().endTick();
                }
            }
        }
        setChanged();
        notifyObservers();
    }
}
