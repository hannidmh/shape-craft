package modele.plateau;

import modele.item.ItemShape;

public final class ScenarioB4Test {
    private ScenarioB4Test() {
    }

    public static void main(String[] args) {
        Plateau plateau = new Plateau();

        Mine mine = new Mine();
        mine.setDirection(Direction.East);

        Tapis tapis = new Tapis();
        tapis.setDirection(Direction.East);

        ZoneLivraison zoneLivraison = new ZoneLivraison();

        plateau.getCases()[1][1].setGisement(new ItemShape("Cr------"));
        plateau.setMachine(1, 1, mine);
        plateau.setMachine(2, 1, tapis);
        plateau.setMachine(3, 1, zoneLivraison);

        assertTrue("mine starts empty", mine.getCurrent() == null);
        assertTrue("tapis starts empty", tapis.getCurrent() == null);
        assertEquals("score starts at zero", 0, zoneLivraison.getScore());

        plateau.run();
        assertTrue("item reaches the tapis after tick 1", tapis.getCurrent() != null);
        assertTrue("delivery stays empty after tick 1", zoneLivraison.getCurrent() == null);
        assertEquals("score unchanged after tick 1", 0, zoneLivraison.getScore());

        plateau.run();
        assertTrue("item reaches the delivery after tick 2", zoneLivraison.getCurrent() != null);
        assertEquals("score unchanged before absorption", 0, zoneLivraison.getScore());

        plateau.run();
        assertEquals("score increments after absorption", 1, zoneLivraison.getScore());

        System.out.println("ScenarioB4Test: OK");
    }

    private static void assertTrue(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void assertEquals(String label, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
