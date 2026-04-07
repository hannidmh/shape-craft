package modele.plateau;

import modele.item.Color;
import modele.item.ItemColor;

public final class MixeurTest {
    private MixeurTest() {
    }

    public static void main(String[] args) {
        shouldAcceptTwoBottomInputsAndMixColors();
        shouldTreatWhiteAsNeutralWhenMixing();
        shouldRejectWrongInputs();
        shouldRotateClockwiseAndMovePorts();
        System.out.println("MixeurTest: OK");
    }

    private static void shouldAcceptTwoBottomInputsAndMixColors() {
        Plateau plateau = new Plateau();

        Mixeur mixeur = new Mixeur();
        plateau.setMachine(5, 5, mixeur);
        plateau.setMachine(6, 5, mixeur);

        Tapis output = new Tapis();
        plateau.setMachine(5, 4, output);

        boolean acceptedLeft = mixeur.receive(new ItemColor(Color.Red),
                plateau.getCases()[5][5], plateau.getCases()[5][6]);
        boolean acceptedRight = mixeur.receive(new ItemColor(Color.Green),
                plateau.getCases()[6][5], plateau.getCases()[6][6]);

        assertTrue("left color input must be accepted from below the left tile", acceptedLeft);
        assertTrue("right color input must be accepted from below the right tile", acceptedRight);

        mixeur.work();
        mixeur.send();
        output.endTick();

        assertColor("red + green must produce yellow", output.getCurrent(), Color.Yellow);
    }

    private static void shouldTreatWhiteAsNeutralWhenMixing() {
        Plateau plateau = new Plateau();

        Mixeur mixeur = new Mixeur();
        plateau.setMachine(5, 5, mixeur);
        plateau.setMachine(6, 5, mixeur);

        Tapis output = new Tapis();
        plateau.setMachine(5, 4, output);

        boolean acceptedLeft = mixeur.receive(new ItemColor(Color.White),
                plateau.getCases()[5][5], plateau.getCases()[5][6]);
        boolean acceptedRight = mixeur.receive(new ItemColor(Color.Red),
                plateau.getCases()[6][5], plateau.getCases()[6][6]);

        assertTrue("white input must still be accepted", acceptedLeft);
        assertTrue("colored input must still be accepted", acceptedRight);

        mixeur.work();
        mixeur.send();
        output.endTick();

        assertColor("white + red must keep red instead of collapsing to white", output.getCurrent(), Color.Red);
    }

    private static void shouldRejectWrongInputs() {
        Plateau plateau = new Plateau();

        Mixeur mixeur = new Mixeur();
        plateau.setMachine(5, 5, mixeur);
        plateau.setMachine(6, 5, mixeur);

        boolean wrongLeftSide = mixeur.receive(new ItemColor(Color.Red),
                plateau.getCases()[5][5], plateau.getCases()[4][5]);
        boolean wrongRightSide = mixeur.receive(new ItemColor(Color.Blue),
                plateau.getCases()[6][5], plateau.getCases()[5][5]);

        assertFalse("left color input must not enter from the side", wrongLeftSide);
        assertFalse("right color input must not enter through the left tile", wrongRightSide);
    }

    private static void shouldRotateClockwiseAndMovePorts() {
        Plateau plateau = new Plateau();

        Mixeur mixeur = new Mixeur();
        plateau.setMachine(5, 5, mixeur);
        plateau.setMachine(6, 5, mixeur);

        boolean rotated = plateau.rotateMachine(mixeur);
        assertTrue("mixer should rotate clockwise", rotated);
        assertSame("primary tile must keep the mixer", mixeur, plateau.getCases()[5][5].getMachine());
        assertSame("secondary tile must move below after rotation", mixeur, plateau.getCases()[5][6].getMachine());
        assertNull("old horizontal extension must be freed", plateau.getCases()[6][5].getMachine());

        Tapis output = new Tapis();
        output.setDirection(Direction.East);
        plateau.setMachine(6, 5, output);

        boolean acceptedLeft = mixeur.receive(new ItemColor(Color.Blue),
                plateau.getCases()[5][5], plateau.getCases()[4][5]);
        boolean acceptedRight = mixeur.receive(new ItemColor(Color.Green),
                plateau.getCases()[5][6], plateau.getCases()[4][6]);

        assertTrue("rotated mixer must accept the left color from the left side", acceptedLeft);
        assertTrue("rotated mixer must accept the right color from the left of the lower tile", acceptedRight);

        mixeur.work();
        mixeur.send();
        output.endTick();

        assertColor("blue + green must produce cyan after rotation", output.getCurrent(), Color.Cyan);
    }

    private static void assertColor(String label, Object item, Color expectedColor) {
        if (!(item instanceof ItemColor itemColor)) {
            throw new AssertionError(label + " but item is " + item);
        }
        if (itemColor.getColor() != expectedColor) {
            throw new AssertionError(label + " expected " + expectedColor + " but got " + itemColor.getColor());
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
