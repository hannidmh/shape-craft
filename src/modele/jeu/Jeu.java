package modele.jeu;

import modele.plateau.Mine;
import modele.plateau.Direction;
import modele.plateau.Plateau;
import modele.plateau.Poubelle;
import modele.plateau.Tapis;
import modele.plateau.Machine;
import modele.plateau.ZoneLivraison;
import modele.plateau.Rotateur;
import modele.plateau.Coupeur;
import modele.plateau.Empileur;
import modele.plateau.Peintre;

public class Jeu extends Thread {
    public enum BuildMode {
        TAPIS, MINE, LIVRAISON, POUBELLE, ROTATEUR, COUPEUR, EMPILEUR, PEINTRE
    }

    private Plateau plateau;
    private int lastPlacedX = -1;
    private int lastPlacedY = -1;
    private BuildMode buildMode = BuildMode.TAPIS;

    public Jeu() {
        plateau = new Plateau();

        plateau.setMachine(5, 10, new Mine());
        plateau.setMachine(8, 8, new Poubelle());
        plateau.setMachine(5, 5, new ZoneLivraison());

        start();

    }

    public Plateau getPlateau() {
        return plateau;
    }

    public BuildMode getBuildMode() {
        return buildMode;
    }

    public void setBuildMode(BuildMode buildMode) {
        this.buildMode = buildMode;
    }

    public void press(int x, int y) {
        lastPlacedX = x;
        lastPlacedY = y;
        if (buildMode == BuildMode.TAPIS) {
            Tapis tapis = placeOrUpdateTapis(x, y, Direction.North);
            if (tapis != null) {
                tapis.setIncomingDirection(null);
            }
            return;
        }
        placeMachine(x, y);
    }

    public void slide(int x, int y) {
        if (x == lastPlacedX && y == lastPlacedY) {
            return;
        }

        if (buildMode != BuildMode.TAPIS) {
            return;
        }

        Direction direction = directionFromDelta(x - lastPlacedX, y - lastPlacedY);
        if (direction == null) {
            lastPlacedX = x;
            lastPlacedY = y;
            return;
        }

        Tapis previousTapis = placeOrUpdateTapis(lastPlacedX, lastPlacedY, direction);
        if (previousTapis == null) {
            lastPlacedX = x;
            lastPlacedY = y;
            return;
        }
        previousTapis.setDirection(direction);

        Tapis currentTapis = placeOrUpdateTapis(x, y, direction);
        if (currentTapis == null) {
            lastPlacedX = x;
            lastPlacedY = y;
            return;
        }
        currentTapis.setIncomingDirection(opposite(direction));
        currentTapis.setDirection(direction);

        lastPlacedX = x;
        lastPlacedY = y;
    }

    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {

        while (true) {
            try {
                plateau.run();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private Tapis placeOrUpdateTapis(int x, int y, Direction direction) {
        Machine machine = plateau.getCases()[x][y].getMachine();

        if (machine instanceof Tapis tapis) {
            if (direction != null) {
                tapis.setDirection(direction);
            }
            return tapis;
        }

        if (machine != null) {
            return null;
        }

        Tapis tapis = new Tapis();
        tapis.setDirection(direction == null ? Direction.North : direction);
        plateau.setMachine(x, y, tapis);
        return tapis;
    }

    private void placeMachine(int x, int y) {
        if (plateau.getCases()[x][y].getMachine() != null) {
            return;
        }

        if (buildMode == BuildMode.COUPEUR) {
            placeCoupeur(x, y);
            return;
        }

        Machine machine = switch (buildMode) {
            case TAPIS -> new Tapis();
            case MINE -> new Mine();
            case LIVRAISON -> new ZoneLivraison();
            case POUBELLE -> new Poubelle();
            case ROTATEUR -> new Rotateur();
            case COUPEUR -> throw new IllegalStateException("Coupeur géré à part");
            case EMPILEUR -> new Empileur();
            case PEINTRE -> new Peintre();
        };
        plateau.setMachine(x, y, machine);
    }

    private void placeCoupeur(int x, int y) {
        int extensionX = x + 1;
        if (extensionX >= Plateau.SIZE_X) {
            return;
        }
        if (plateau.getCases()[extensionX][y].getMachine() != null) {
            return;
        }

        Coupeur coupeur = new Coupeur();
        plateau.setMachine(x, y, coupeur);
        plateau.setMachine(extensionX, y, coupeur);
    }

    private Direction directionFromDelta(int dx, int dy) {
        if (dx == 1 && dy == 0) {
            return Direction.East;
        }
        if (dx == -1 && dy == 0) {
            return Direction.West;
        }
        if (dx == 0 && dy == 1) {
            return Direction.South;
        }
        if (dx == 0 && dy == -1) {
            return Direction.North;
        }
        return null;
    }

    private Direction opposite(Direction direction) {
        return switch (direction) {
            case North -> Direction.South;
            case South -> Direction.North;
            case East -> Direction.West;
            case West -> Direction.East;
        };
    }

}
