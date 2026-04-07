package modele.plateau;

import modele.item.ItemShape;

public final class TunnelTest {
    private TunnelTest() {
    }

    public static void main(String[] args) {
        shouldTeleportItemToTheExitTunnelLane();
        shouldKeepItemWhenNoCompatibleExitExists();
        System.out.println("TunnelTest: OK");
    }

    private static void shouldTeleportItemToTheExitTunnelLane() {
        Plateau plateau = new Plateau();

        plateau.getCases()[1][1].setGisement(new ItemShape(ItemShape.ShapeType.SQUARE));

        Mine mine = new Mine();
        mine.setDirection(Direction.East);
        plateau.setMachine(1, 1, mine);

        Tunnel entryTunnel = new Tunnel();
        entryTunnel.setDirection(Direction.East);
        plateau.setMachine(2, 1, entryTunnel);

        Tunnel exitTunnel = new Tunnel();
        exitTunnel.setDirection(Direction.West);
        plateau.setMachine(5, 1, exitTunnel);

        Tapis output = new Tapis();
        output.setDirection(Direction.East);
        plateau.setMachine(6, 1, output);

        ZoneLivraison zoneLivraison = new ZoneLivraison();
        plateau.setMachine(7, 1, zoneLivraison);

        plateau.run();
        assertTrue("entry tunnel must store the item after tick 1", entryTunnel.getCurrent() != null);

        plateau.run();
        assertTrue("the item must appear after the exit tunnel on tick 2", output.getCurrent() != null);

        plateau.run();
        assertTrue("the delivered item must reach the hub on tick 3", zoneLivraison.getCurrent() != null);

        plateau.run();
        assertEquals("the hub score must increase after teleportation", 1, zoneLivraison.getScore());
    }

    private static void shouldKeepItemWhenNoCompatibleExitExists() {
        Plateau plateau = new Plateau();

        Tunnel entryTunnel = new Tunnel();
        entryTunnel.setDirection(Direction.East);
        plateau.setMachine(3, 3, entryTunnel);
        entryTunnel.current.add(new ItemShape(ItemShape.ShapeType.CIRCLE));

        Tunnel misalignedTunnel = new Tunnel();
        misalignedTunnel.setDirection(Direction.North);
        plateau.setMachine(6, 3, misalignedTunnel);

        plateau.run();

        assertTrue("without a compatible tunnel ahead the item must stay inside the entry tunnel",
                entryTunnel.getCurrent() != null);
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
