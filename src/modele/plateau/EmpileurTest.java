package modele.plateau;

import modele.item.ItemShape;

public final class EmpileurTest {
    private EmpileurTest() {
    }

    public static void main(String[] args) {
        shouldAcceptLeftAsBottomAndRightAsTop();
        shouldRejectWrongInputs();
        shouldRotateClockwiseAndMovePorts();
        System.out.println("EmpileurTest: OK");
    }

    private static void shouldAcceptLeftAsBottomAndRightAsTop() {
        Plateau plateau = new Plateau();

        Empileur empileur = new Empileur();
        plateau.setMachine(5, 5, empileur);
        plateau.setMachine(6, 5, empileur);

        Tapis output = new Tapis();
        plateau.setMachine(5, 4, output);

        boolean acceptedBottom = empileur.receive(new ItemShape(ItemShape.ShapeType.CIRCLE),
                plateau.getCases()[5][5], plateau.getCases()[5][6]);
        boolean acceptedTop = empileur.receive(new ItemShape(ItemShape.ShapeType.STAR),
                plateau.getCases()[6][5], plateau.getCases()[6][6]);

        assertTrue("left input must be accepted as the bottom shape", acceptedBottom);
        assertTrue("right input must be accepted as the top shape", acceptedTop);

        empileur.work();
        empileur.send();
        output.endTick();

        assertStack("stacker output must keep the left shape at the bottom and the right shape on top",
                output.getCurrent(), ItemShape.ShapeType.CIRCLE, ItemShape.ShapeType.STAR);
    }

    private static void shouldRejectWrongInputs() {
        Plateau plateau = new Plateau();

        Empileur empileur = new Empileur();
        plateau.setMachine(5, 5, empileur);
        plateau.setMachine(6, 5, empileur);

        boolean wrongBottomSide = empileur.receive(new ItemShape(ItemShape.ShapeType.SQUARE),
                plateau.getCases()[5][5], plateau.getCases()[4][5]);
        boolean wrongTopSide = empileur.receive(new ItemShape(ItemShape.ShapeType.FAN),
                plateau.getCases()[6][5], plateau.getCases()[5][5]);

        assertFalse("bottom input must not enter from the left", wrongBottomSide);
        assertFalse("top input must not enter through the left tile", wrongTopSide);
    }

    private static void shouldRotateClockwiseAndMovePorts() {
        Plateau plateau = new Plateau();

        Empileur empileur = new Empileur();
        plateau.setMachine(5, 5, empileur);
        plateau.setMachine(6, 5, empileur);

        boolean rotated = plateau.rotateMachine(empileur);
        assertTrue("stacker should rotate clockwise", rotated);
        assertSame("primary tile must keep the stacker", empileur, plateau.getCases()[5][5].getMachine());
        assertSame("secondary tile must move below after rotation", empileur, plateau.getCases()[5][6].getMachine());
        assertNull("old horizontal extension must be freed", plateau.getCases()[6][5].getMachine());

        Tapis output = new Tapis();
        output.setDirection(Direction.East);
        plateau.setMachine(6, 5, output);

        boolean acceptedBottom = empileur.receive(new ItemShape(ItemShape.ShapeType.SQUARE),
                plateau.getCases()[5][5], plateau.getCases()[4][5]);
        boolean acceptedTop = empileur.receive(new ItemShape(ItemShape.ShapeType.FAN),
                plateau.getCases()[5][6], plateau.getCases()[4][6]);

        assertTrue("rotated stacker must accept the bottom item from the left", acceptedBottom);
        assertTrue("rotated stacker must accept the top item from the left of the lower tile", acceptedTop);

        empileur.work();
        empileur.send();
        output.endTick();

        assertStack("rotated stacker must still output the stacked result", output.getCurrent(),
                ItemShape.ShapeType.SQUARE, ItemShape.ShapeType.FAN);
    }

    private static void assertStack(String label, Object item,
            ItemShape.ShapeType expectedBottom, ItemShape.ShapeType expectedTop) {
        if (!(item instanceof ItemShape shape)) {
            throw new AssertionError(label + " but item is " + item);
        }
        if (shape.getType() != expectedBottom) {
            throw new AssertionError(label + " expected bottom " + expectedBottom + " but got " + shape.getType());
        }
        ItemShape top = shape.getStackedTop();
        if (top == null) {
            throw new AssertionError(label + " but top layer is missing");
        }
        if (top.getType() != expectedTop) {
            throw new AssertionError(label + " expected top " + expectedTop + " but got " + top.getType());
        }
    }

    private static void assertTrue(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void assertFalse(String label, boolean condition) {
        if (condition) {
            throw new AssertionError(label);
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
