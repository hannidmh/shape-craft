package modele.plateau;

import modele.item.Color;
import modele.item.ItemColor;
import modele.item.ItemShape;

public final class EditionPlateauTest {
    private EditionPlateauTest() {
    }

    public static void main(String[] args) {
        shouldExposeTopDepositsForEachColor();
        shouldExposeBottomDepositsForEachMineShape();
        shouldRemoveSingleTileMachine();
        shouldClearItemsStoredInDeletedMachine();
        shouldRemoveWholeCutter();
        shouldClearPendingCutterOutputsWhenRemoved();
        shouldRefuseRotationWhenTargetTilesAreOccupied();
        shouldNotRemoveHub();
        shouldAcceptOnlyHalfCircleInHub();
        shouldExposeHalfCircleProgressLabel();
        System.out.println("EditionPlateauTest: OK");
    }

    private static void shouldExposeTopDepositsForEachColor() {
        Plateau plateau = new Plateau();

        assertColorDeposit("red deposit must be on top row", plateau, 0, 0, Color.Red);
        assertColorDeposit("green deposit must be on top row", plateau, 4, 0, Color.Green);
        assertColorDeposit("blue deposit must be on top row", plateau, 8, 0, Color.Blue);
        assertNoDeposit("no secondary color deposit must remain at x=12,y=0", plateau, 12, 0);
        assertNoDeposit("no secondary color deposit must remain at x=1,y=1", plateau, 1, 1);
        assertNoDeposit("no secondary color deposit must remain at x=5,y=1", plateau, 5, 1);
        assertNoDeposit("no secondary color deposit must remain at x=9,y=1", plateau, 9, 1);
    }

    private static void shouldExposeBottomDepositsForEachMineShape() {
        Plateau plateau = new Plateau();

        assertDepositShape("circle deposit must be at bottom left", plateau, 1, Plateau.SIZE_Y - 1, ItemShape.ShapeType.CIRCLE);
        assertDepositShape("circle deposit must span 3 tiles", plateau, 3, Plateau.SIZE_Y - 1, ItemShape.ShapeType.CIRCLE);
        assertDepositShape("square deposit must be on bottom row", plateau, 5, Plateau.SIZE_Y - 1, ItemShape.ShapeType.SQUARE);
        assertDepositShape("star deposit must be on bottom row", plateau, 9, Plateau.SIZE_Y - 1, ItemShape.ShapeType.STAR);
        assertDepositShape("fan deposit must be on bottom row", plateau, 13, Plateau.SIZE_Y - 1, ItemShape.ShapeType.FAN);
    }

    private static void shouldRemoveSingleTileMachine() {
        Plateau plateau = new Plateau();
        plateau.setMachine(1, 1, new Mine());

        boolean removed = plateau.removeMachine(1, 1);

        assertTrue("mine should be removed", removed);
        assertNull("mine tile should be empty", plateau.getCases()[1][1].getMachine());
    }

    private static void shouldRemoveWholeCutter() {
        Plateau plateau = new Plateau();
        Coupeur coupeur = new Coupeur();
        plateau.setMachine(3, 3, coupeur);
        plateau.setMachine(4, 3, coupeur);

        boolean removed = plateau.removeMachine(4, 3);

        assertTrue("cutter should be removed from secondary tile", removed);
        assertNull("primary cutter tile should be empty", plateau.getCases()[3][3].getMachine());
        assertNull("secondary cutter tile should be empty", plateau.getCases()[4][3].getMachine());
    }

    private static void shouldClearItemsStoredInDeletedMachine() {
        Plateau plateau = new Plateau();
        Tapis tapis = new Tapis();
        plateau.setMachine(2, 2, tapis);
        tapis.current.add(new ItemShape(ItemShape.ShapeType.CIRCLE));
        tapis.incoming.add(new ItemShape(ItemShape.ShapeType.STAR));

        boolean removed = plateau.removeMachine(2, 2);

        assertTrue("belt should be removed", removed);
        assertEquals("current items must be cleared on deletion", 0, tapis.current.size());
        assertEquals("incoming items must be cleared on deletion", 0, tapis.incoming.size());
    }

    private static void shouldClearPendingCutterOutputsWhenRemoved() {
        Plateau plateau = new Plateau();
        Coupeur coupeur = new Coupeur();
        plateau.setMachine(3, 3, coupeur);
        plateau.setMachine(4, 3, coupeur);
        coupeur.current.add(new ItemShape(ItemShape.ShapeType.SQUARE));

        coupeur.work();
        boolean removed = plateau.removeMachine(3, 3);

        assertTrue("cutter should be removed", removed);
        assertNull("left pending output must be cleared", coupeur.getDisplayedItem(coupeur.getCase()));
    }

    private static void shouldRefuseRotationWhenTargetTilesAreOccupied() {
        Plateau plateau = new Plateau();
        Coupeur coupeur = new Coupeur();
        plateau.setMachine(3, 3, coupeur);
        plateau.setMachine(4, 3, coupeur);
        plateau.setMachine(3, 4, new Mine());

        boolean rotated = plateau.rotateMachine(coupeur);

        assertFalse("cutter rotation must fail when the target tile is occupied", rotated);
        assertSame("cutter must remain on its primary tile", coupeur, plateau.getCases()[3][3].getMachine());
        assertSame("cutter must remain on its original secondary tile", coupeur, plateau.getCases()[4][3].getMachine());
        assertDirection("cutter direction must stay north", Direction.North, coupeur.getDirection());
    }

    private static void shouldNotRemoveHub() {
        Plateau plateau = new Plateau();
        ZoneLivraison hub = new ZoneLivraison();

        for (int dx = 0; dx < 4; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                plateau.setMachine(6 + dx, 6 + dy, hub);
            }
        }

        boolean removed = plateau.removeMachine(7, 7);

        assertFalse("hub must not be removable", removed);
        assertSame("hub must still be present", hub, plateau.getCases()[7][7].getMachine());
    }

    private static void shouldAcceptOnlyHalfCircleInHub() {
        ZoneLivraison hub = new ZoneLivraison();
        hub.configureHalfCircleGoal(10);

        hub.incoming.add(new ItemShape(ItemShape.ShapeType.CIRCLE));
        hub.endTick();
        hub.work();
        assertEquals("full circle must be rejected", 0, hub.getScore());

        hub.incoming.add(new ItemShape(ItemShape.ShapeType.CIRCLE, modele.item.Color.Gray, ItemShape.Part.LEFT));
        hub.endTick();
        hub.work();
        assertEquals("left half circle must be accepted", 1, hub.getScore());

        hub.incoming.add(new ItemShape(ItemShape.ShapeType.CIRCLE, modele.item.Color.Gray, ItemShape.Part.RIGHT));
        hub.endTick();
        hub.work();
        assertEquals("right half circle must also be accepted", 2, hub.getScore());
    }

    private static void shouldExposeHalfCircleProgressLabel() {
        ZoneLivraison hub = new ZoneLivraison();
        hub.configureHalfCircleGoal(10);

        assertEquals("required deliveries must be 10", 10, hub.getRequiredDeliveries());
        assertStringEquals("initial progress label must be 0/10", "0/10 livre", hub.getProgressLabel());

        hub.incoming.add(new ItemShape(ItemShape.ShapeType.CIRCLE, modele.item.Color.Gray, ItemShape.Part.LEFT));
        hub.endTick();
        hub.work();

        assertStringEquals("progress label must update after delivery", "1/10 livre", hub.getProgressLabel());
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

    private static void assertNull(String label, Object value) {
        if (value != null) {
            throw new AssertionError(label + " but got " + value);
        }
    }

    private static void assertSame(String label, Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError(label);
        }
    }

    private static void assertEquals(String label, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertStringEquals(String label, String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertDirection(String label, Direction expected, Direction actual) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertDepositShape(String label, Plateau plateau, int x, int y, ItemShape.ShapeType expectedType) {
        Object gisement = plateau.getCases()[x][y].getGisement();
        if (!(gisement instanceof ItemShape shape)) {
            throw new AssertionError(label + " but tile has no shape deposit");
        }
        if (shape.getType() != expectedType) {
            throw new AssertionError(label + " expected " + expectedType + " but got " + shape.getType());
        }
    }

    private static void assertColorDeposit(String label, Plateau plateau, int x, int y, Color expectedColor) {
        Object gisement = plateau.getCases()[x][y].getGisement();
        if (!(gisement instanceof ItemColor itemColor)) {
            throw new AssertionError(label + " but tile has no color deposit");
        }
        if (itemColor.getColor() != expectedColor) {
            throw new AssertionError(label + " expected " + expectedColor + " but got " + itemColor.getColor());
        }
    }

    private static void assertNoDeposit(String label, Plateau plateau, int x, int y) {
        if (plateau.getCases()[x][y].getGisement() != null) {
            throw new AssertionError(label);
        }
    }
}
