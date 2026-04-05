package modele.plateau;

import modele.item.ItemShape;

public final class TapisTest {
    private TapisTest() {
    }

    public static void main(String[] args) {
        shouldAcceptOnlyTheExpectedInputForStraightBelts();
        shouldAcceptOnlyTheConfiguredIncomingSideForTurns();
        shouldRotateIncomingSideClockwise();
        System.out.println("TapisTest: OK");
    }

    private static void shouldAcceptOnlyTheExpectedInputForStraightBelts() {
        Plateau plateau = new Plateau();

        Tapis target = new Tapis();
        target.setDirection(Direction.North);
        plateau.setMachine(5, 5, target);

        Tapis wrongSource = new Tapis();
        wrongSource.setDirection(Direction.East);
        plateau.setMachine(4, 5, wrongSource);
        wrongSource.current.add(new ItemShape(ItemShape.ShapeType.STAR));

        Tapis validSource = new Tapis();
        validSource.setDirection(Direction.North);
        plateau.setMachine(5, 6, validSource);
        validSource.current.add(new ItemShape(ItemShape.ShapeType.CIRCLE));

        plateau.run();

        assertShapeType("straight belt must accept the item from its back side", target.getCurrent(),
                ItemShape.ShapeType.CIRCLE);
        assertShapeType("wrong side source must keep its item", wrongSource.getCurrent(), ItemShape.ShapeType.STAR);
    }

    private static void shouldAcceptOnlyTheConfiguredIncomingSideForTurns() {
        Plateau plateau = new Plateau();

        Tapis target = new Tapis();
        target.setDirection(Direction.East);
        target.setIncomingDirection(Direction.South);
        plateau.setMachine(5, 5, target);

        Tapis wrongSource = new Tapis();
        wrongSource.setDirection(Direction.East);
        plateau.setMachine(4, 5, wrongSource);
        wrongSource.current.add(new ItemShape(ItemShape.ShapeType.SQUARE));

        Tapis validSource = new Tapis();
        validSource.setDirection(Direction.North);
        plateau.setMachine(5, 6, validSource);
        validSource.current.add(new ItemShape(ItemShape.ShapeType.FAN));

        plateau.run();

        assertShapeType("turned belt must only accept the configured incoming side", target.getCurrent(),
                ItemShape.ShapeType.FAN);
        assertShapeType("left source must not inject into a south->east turn", wrongSource.getCurrent(),
                ItemShape.ShapeType.SQUARE);
    }

    private static void shouldRotateIncomingSideClockwise() {
        Plateau plateau = new Plateau();

        Tapis target = new Tapis();
        target.setDirection(Direction.East);
        target.setIncomingDirection(Direction.South);
        plateau.setMachine(5, 5, target);

        boolean rotated = plateau.rotateMachine(target);
        assertTrue("belt rotation must succeed", rotated);
        assertDirection("belt output must rotate clockwise", Direction.South, target.getDirection());
        assertDirection("belt input must rotate clockwise too", Direction.West, target.getIncomingDirection());

        Tapis validSource = new Tapis();
        validSource.setDirection(Direction.East);
        plateau.setMachine(4, 5, validSource);
        validSource.current.add(new ItemShape(ItemShape.ShapeType.STAR));

        Tapis wrongSource = new Tapis();
        wrongSource.setDirection(Direction.North);
        plateau.setMachine(5, 6, wrongSource);
        wrongSource.current.add(new ItemShape(ItemShape.ShapeType.CIRCLE));

        plateau.run();

        assertShapeType("rotated belt must now accept from the west", target.getCurrent(), ItemShape.ShapeType.STAR);
        assertShapeType("old south input must now be rejected", wrongSource.getCurrent(), ItemShape.ShapeType.CIRCLE);
    }

    private static void assertShapeType(String label, Object item, ItemShape.ShapeType expectedType) {
        if (!(item instanceof ItemShape shape)) {
            throw new AssertionError(label + " but item is " + item);
        }
        if (shape.getType() != expectedType) {
            throw new AssertionError(label + " expected " + expectedType + " but got " + shape.getType());
        }
    }

    private static void assertTrue(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void assertDirection(String label, Direction expected, Direction actual) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
