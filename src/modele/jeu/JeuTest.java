package modele.jeu;

import modele.plateau.Direction;
import modele.plateau.Poubelle;
import modele.plateau.Tapis;

public final class JeuTest {
    private JeuTest() {
    }

    public static void main(String[] args) {
        shouldAllowDraggingBeltsFromMachineIntoSingleTileTurn();
        System.out.println("JeuTest: OK");
    }

    private static void shouldAllowDraggingBeltsFromMachineIntoSingleTileTurn() {
        Jeu jeu = new Jeu(false);
        jeu.getPlateau().setMachine(4, 5, new Poubelle());

        jeu.press(4, 5);
        jeu.slide(5, 5);
        jeu.slide(5, 6);

        Object machine = jeu.getPlateau().getCases()[5][5].getMachine();
        if (!(machine instanceof Tapis tapis)) {
            throw new AssertionError("the tile next to the machine must contain a belt");
        }

        assertDirection("the turn must send the item downward", Direction.South, tapis.getDirection());
        assertDirection("the turn must still accept items from the machine side", Direction.West,
                tapis.getIncomingDirection());
    }

    private static void assertDirection(String label, Direction expected, Direction actual) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
