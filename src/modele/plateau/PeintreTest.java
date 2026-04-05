package modele.plateau;

import modele.item.Color;
import modele.item.ItemColor;
import modele.item.ItemShape;

public final class PeintreTest {
    private PeintreTest() {
    }

    public static void main(String[] args) {
        shouldAcceptShapeOnLeftAndColorOnTopRight();
        shouldRejectWrongInputs();
        shouldRotateClockwiseAndMoveInputsAndOutput();
        System.out.println("PeintreTest: OK");
    }

    private static void shouldAcceptShapeOnLeftAndColorOnTopRight() {
        Plateau plateau = new Plateau();

        Peintre peintre = new Peintre();
        plateau.setMachine(5, 5, peintre);
        plateau.setMachine(6, 5, peintre);

        Tapis output = new Tapis();
        output.setDirection(Direction.East);
        plateau.setMachine(7, 5, output);

        Case leftInputCase = plateau.getCases()[4][5];
        Case topRightInputCase = plateau.getCases()[6][4];

        boolean acceptedShape = peintre.receive(new ItemShape(ItemShape.ShapeType.STAR), plateau.getCases()[5][5], leftInputCase);
        boolean acceptedColor = peintre.receive(new ItemColor(Color.Red), plateau.getCases()[6][5], topRightInputCase);

        assertTrue("shape input must be accepted on the left of the painter", acceptedShape);
        assertTrue("color input must be accepted above the right tile", acceptedColor);

        peintre.work();
        peintre.send();
        output.endTick();

        if (!(output.getCurrent() instanceof ItemShape result)) {
            throw new AssertionError("painted shape must be sent to the right output");
        }
        assertEquals("painted shape must keep its type", ItemShape.ShapeType.STAR, result.getType());
        assertEquals("painted shape must receive the input color", Color.Red, result.getColor());
    }

    private static void shouldRejectWrongInputs() {
        Plateau plateau = new Plateau();

        Peintre peintre = new Peintre();
        plateau.setMachine(5, 5, peintre);
        plateau.setMachine(6, 5, peintre);

        boolean wrongShapeSide = peintre.receive(new ItemShape(ItemShape.ShapeType.CIRCLE),
                plateau.getCases()[6][5], plateau.getCases()[5][5]);
        boolean wrongColorSide = peintre.receive(new ItemColor(Color.Blue),
                plateau.getCases()[5][5], plateau.getCases()[5][4]);

        assertFalse("shape must not enter through the right tile", wrongShapeSide);
        assertFalse("color must not enter through the left tile", wrongColorSide);
    }

    private static void shouldRotateClockwiseAndMoveInputsAndOutput() {
        Plateau plateau = new Plateau();

        Peintre peintre = new Peintre();
        plateau.setMachine(5, 5, peintre);
        plateau.setMachine(6, 5, peintre);

        boolean rotated = plateau.rotateMachine(peintre);
        assertTrue("painter should rotate clockwise", rotated);

        Tapis output = new Tapis();
        output.setDirection(Direction.South);
        plateau.setMachine(5, 7, output);

        Case topInputCase = plateau.getCases()[5][4];
        Case rightInputCase = plateau.getCases()[6][6];

        boolean acceptedShape = peintre.receive(new ItemShape(ItemShape.ShapeType.FAN),
                plateau.getCases()[5][5], topInputCase);
        boolean acceptedColor = peintre.receive(new ItemColor(Color.Blue),
                plateau.getCases()[5][6], rightInputCase);

        assertTrue("shape input must rotate to the top of the painter", acceptedShape);
        assertTrue("color input must rotate to the right of the lower tile", acceptedColor);

        peintre.work();
        peintre.send();
        output.endTick();

        if (!(output.getCurrent() instanceof ItemShape result)) {
            throw new AssertionError("painted shape must be sent to the rotated output");
        }
        assertEquals("painted shape must keep its type after rotation", ItemShape.ShapeType.FAN, result.getType());
        assertEquals("painted shape must receive the rotated input color", Color.Blue, result.getColor());
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

    private static void assertEquals(String label, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
