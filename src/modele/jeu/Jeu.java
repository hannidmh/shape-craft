package modele.jeu;

import modele.plateau.Mine;
import modele.plateau.Direction;
import modele.plateau.Plateau;
import modele.plateau.Poubelle;
import modele.plateau.Tapis;
import modele.plateau.Tunnel;
import modele.plateau.Machine;
import modele.plateau.ZoneLivraison;
import modele.plateau.Rotateur;
import modele.plateau.Coupeur;
import modele.plateau.Empileur;
import modele.plateau.Mixeur;
import modele.plateau.Peintre;

public class Jeu extends Thread {
    public enum BuildMode {
        TAPIS, TUNNEL, MINE, LIVRAISON, POUBELLE, ROTATEUR, COUPEUR, EMPILEUR, MIXEUR, PEINTRE
    }

    private Plateau plateau;
    private int lastPlacedX = -1;
    private int lastPlacedY = -1;
    private BuildMode buildMode = BuildMode.TAPIS;
    private Machine selectedMachine;

    public Jeu() {
        this(true);
    }

    public Jeu(boolean autoStart) {
        plateau = new Plateau();

        placeHub(6, 6, createDefaultHub());

        if (autoStart) {
            start();
        }

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
        Machine existingMachine = plateau.getCases()[x][y].getMachine();
        if (existingMachine != null) {
            selectMachine(existingMachine);
            if (!(existingMachine instanceof Tapis) || buildMode != BuildMode.TAPIS) {
                return;
            }
            return;
        }

        if (buildMode == BuildMode.TAPIS) {
            Tapis tapis = placeOrUpdateTapis(x, y, Direction.North);
            if (tapis != null) {
                tapis.setIncomingDirection(null);
                selectMachine(tapis);
            } else {
                clearSelection();
            }
            return;
        }
        placeMachine(x, y);
    }

    public void erase(int x, int y) {
        Machine targetMachine = plateau.getCases()[x][y].getMachine();
        boolean removed = plateau.removeMachine(x, y);
        if (removed && targetMachine != null && targetMachine == getSelectedMachine()) {
            clearSelection();
        }
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

        Machine sourceMachine = plateau.getCases()[lastPlacedX][lastPlacedY].getMachine();
        Tapis previousTapis = placeOrUpdateTapis(lastPlacedX, lastPlacedY, direction);
        boolean startsFromPlacedMachine = previousTapis == null
                && sourceMachine != null
                && !(sourceMachine instanceof Tapis);
        if (previousTapis == null && !startsFromPlacedMachine) {
            lastPlacedX = x;
            lastPlacedY = y;
            return;
        }

        Tapis currentTapis = placeOrUpdateTapis(x, y, direction);
        if (currentTapis == null) {
            lastPlacedX = x;
            lastPlacedY = y;
            return;
        }
        currentTapis.setIncomingDirection(opposite(direction));
        currentTapis.setDirection(direction);
        selectMachine(currentTapis);

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

    public boolean rotateSelection() {
        Machine machine = getSelectedMachine();
        if (machine == null) {
            return false;
        }
        return plateau.rotateMachine(machine);
    }

    public boolean isSelectedMachine(Machine machine) {
        return machine != null && machine == getSelectedMachine();
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
        Machine existingMachine = plateau.getCases()[x][y].getMachine();
        if (existingMachine != null) {
            selectMachine(existingMachine);
            return;
        }

        if (buildMode == BuildMode.COUPEUR) {
            placeCoupeur(x, y);
            return;
        }
        if (buildMode == BuildMode.PEINTRE) {
            placePeintre(x, y);
            return;
        }
        if (buildMode == BuildMode.EMPILEUR) {
            placeEmpileur(x, y);
            return;
        }
        if (buildMode == BuildMode.MIXEUR) {
            placeMixeur(x, y);
            return;
        }
        if (buildMode == BuildMode.LIVRAISON) {
            clearSelection();
            return;
        }

        Machine machine = switch (buildMode) {
            case TAPIS -> new Tapis();
            case TUNNEL -> createTunnelWithPlacementImageRole();
            case MINE -> plateau.getCases()[x][y].getGisement() == null ? null : new Mine();
            case LIVRAISON -> new ZoneLivraison(); // non utilisé (géré ci-dessus)
            case POUBELLE -> new Poubelle();
            case ROTATEUR -> new Rotateur();
            case COUPEUR -> throw new IllegalStateException("Coupeur géré à part");
            case EMPILEUR -> throw new IllegalStateException("Empileur géré à part");
            case MIXEUR -> throw new IllegalStateException("Mixeur géré à part");
            case PEINTRE -> throw new IllegalStateException("Peintre géré à part");
        };
        if (machine != null) {
            plateau.setMachine(x, y, machine);
            selectMachine(machine);
        } else {
            clearSelection();
        }
    }

    private void placeCoupeur(int x, int y) {
        int extensionX = x + 1;
        if (extensionX >= Plateau.SIZE_X) {
            clearSelection();
            return;
        }
        if (plateau.getCases()[extensionX][y].getMachine() != null) {
            clearSelection();
            return;
        }

        Coupeur coupeur = new Coupeur();
        plateau.setMachine(x, y, coupeur);
        plateau.setMachine(extensionX, y, coupeur);
        selectMachine(coupeur);
    }

    private void placePeintre(int x, int y) {
        int extensionX = x + 1;
        if (extensionX >= Plateau.SIZE_X) {
            clearSelection();
            return;
        }
        if (plateau.getCases()[extensionX][y].getMachine() != null) {
            clearSelection();
            return;
        }

        Peintre peintre = new Peintre();
        plateau.setMachine(x, y, peintre);
        plateau.setMachine(extensionX, y, peintre);
        selectMachine(peintre);
    }

    private void placeEmpileur(int x, int y) {
        int extensionX = x + 1;
        if (extensionX >= Plateau.SIZE_X) {
            clearSelection();
            return;
        }
        if (plateau.getCases()[extensionX][y].getMachine() != null) {
            clearSelection();
            return;
        }

        Empileur empileur = new Empileur();
        plateau.setMachine(x, y, empileur);
        plateau.setMachine(extensionX, y, empileur);
        selectMachine(empileur);
    }

    private void placeMixeur(int x, int y) {
        int extensionX = x + 1;
        if (extensionX >= Plateau.SIZE_X) {
            clearSelection();
            return;
        }
        if (plateau.getCases()[extensionX][y].getMachine() != null) {
            clearSelection();
            return;
        }

        Mixeur mixeur = new Mixeur();
        plateau.setMachine(x, y, mixeur);
        plateau.setMachine(extensionX, y, mixeur);
        selectMachine(mixeur);
    }

    /**
     * Place un hub 4x4 à partir de la coordonnée (x,y) (coin haut-gauche). Toutes
     * les cases doivent être libres et dans la grille. La même instance est
     * référencée sur les 16 cases ; seule la case principale (x,y) exécute run().
     */
    private void placeHub(int x, int y, ZoneLivraison hub) {
        if (x + 3 >= Plateau.SIZE_X || y + 3 >= Plateau.SIZE_Y) {
            return;
        }
        // vérification de disponibilité
        for (int dx = 0; dx < 4; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                if (plateau.getCases()[x + dx][y + dy].getMachine() != null) {
                    return;
                }
            }
        }
        // placement
        for (int dx = 0; dx < 4; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                plateau.setMachine(x + dx, y + dy, hub);
            }
        }
    }

    private ZoneLivraison createDefaultHub() {
        ZoneLivraison hub = new ZoneLivraison();
        hub.configureHalfCircleGoal(10);
        return hub;
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

    private Tunnel createTunnelWithPlacementImageRole() {
        Tunnel tunnel = new Tunnel();
        tunnel.setUsesEntryImage(countPlacedTunnels() % 2 == 0);
        return tunnel;
    }

    private int countPlacedTunnels() {
        int tunnelCount = 0;
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                if (plateau.getCases()[x][y].getMachine() instanceof Tunnel) {
                    tunnelCount++;
                }
            }
        }
        return tunnelCount;
    }

    private Machine getSelectedMachine() {
        if (selectedMachine != null && !plateau.containsMachine(selectedMachine)) {
            selectedMachine = null;
        }
        return selectedMachine;
    }

    private void selectMachine(Machine machine) {
        if (machine instanceof ZoneLivraison) {
            clearSelection();
            return;
        }
        selectedMachine = machine;
    }

    private void clearSelection() {
        selectedMachine = null;
    }

}
