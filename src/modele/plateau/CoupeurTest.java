package modele.plateau;

import modele.item.ItemShape;

public final class CoupeurTest {
    private CoupeurTest() {
    }

    public static void main(String[] args) {
        shouldSendLeftAndRightToTheTwoFrontTiles();
        shouldKeepPendingHalfOnItsOwnSide();
        shouldRotateClockwiseAndKeepOutputsAligned();
        System.out.println("CoupeurTest: OK");
    }

    private static void shouldSendLeftAndRightToTheTwoFrontTiles() {
        Plateau plateau = new Plateau();

        Coupeur coupeur = new Coupeur();
        plateau.setMachine(5, 5, coupeur);
        plateau.setMachine(6, 5, coupeur);

        Tapis leftBelt = new Tapis();
        Tapis rightBelt = new Tapis();
        plateau.setMachine(5, 4, leftBelt);
        plateau.setMachine(6, 4, rightBelt);

        coupeur.current.add(new ItemShape(ItemShape.ShapeType.STAR));

        plateau.run();

        assertShapePart("left output must receive left half", leftBelt.getCurrent(), ItemShape.Part.LEFT);
        assertShapePart("right output must receive right half", rightBelt.getCurrent(), ItemShape.Part.RIGHT);
    }

    private static void shouldKeepPendingHalfOnItsOwnSide() {
        Plateau plateau = new Plateau();

        Coupeur coupeur = new Coupeur();
        plateau.setMachine(5, 5, coupeur);
        plateau.setMachine(6, 5, coupeur);

        Tapis rightBelt = new Tapis();
        plateau.setMachine(6, 4, rightBelt);

        coupeur.current.add(new ItemShape(ItemShape.ShapeType.SQUARE));

        plateau.run();

        assertShapePart("left half must stay pending on the left side", coupeur.getDisplayedItem(coupeur.getCase()),
                ItemShape.Part.LEFT);
        assertShapePart("right output must still be the right half", rightBelt.getCurrent(), ItemShape.Part.RIGHT);

        Tapis leftBelt = new Tapis();
        plateau.setMachine(5, 4, leftBelt);

        plateau.run();

        assertShapePart("pending left half must be sent to the left output tile", leftBelt.getCurrent(), ItemShape.Part.LEFT);
    }

    private static void shouldRotateClockwiseAndKeepOutputsAligned() {
        Plateau plateau = new Plateau();

        Coupeur coupeur = new Coupeur();
        plateau.setMachine(5, 5, coupeur);
        plateau.setMachine(6, 5, coupeur);

        boolean rotated = plateau.rotateMachine(coupeur);

        if (!rotated) {
            throw new AssertionError("cutter should rotate clockwise");
        }
        assertSame("primary tile must keep the cutter", coupeur, plateau.getCases()[5][5].getMachine());
        assertSame("secondary tile must move below after rotation", coupeur, plateau.getCases()[5][6].getMachine());
        assertNull("old horizontal extension must be freed", plateau.getCases()[6][5].getMachine());

        Tapis topOutput = new Tapis();
        Tapis bottomOutput = new Tapis();
        topOutput.setDirection(Direction.East);
        bottomOutput.setDirection(Direction.East);
        plateau.setMachine(6, 5, topOutput);
        plateau.setMachine(6, 6, bottomOutput);

        coupeur.current.add(new ItemShape(ItemShape.ShapeType.STAR));

        plateau.run();

        assertShapePart("top output must still receive the left half after rotation", topOutput.getCurrent(),
                ItemShape.Part.LEFT);
        assertShapePart("bottom output must still receive the right half after rotation", bottomOutput.getCurrent(),
                ItemShape.Part.RIGHT);
    }

    private static void assertShapePart(String label, Object item, ItemShape.Part expectedPart) {
        if (!(item instanceof ItemShape shape)) {
            throw new AssertionError(label + " but item is " + item);
        }
        if (shape.getPart() != expectedPart) {
            throw new AssertionError(label + " expected " + expectedPart + " but got " + shape.getPart());
        }
    }

    private static void assertSame(String label, Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError(label);
        }
    }

    private static void assertNull(String label, Object value) {
        if (value != null) {
            throw new AssertionError(label + " but got " + value);
        }
    }
}
