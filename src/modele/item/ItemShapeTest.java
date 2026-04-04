package modele.item;

public final class ItemShapeTest {
    private ItemShapeTest() {
    }

    public static void main(String[] args) {
        testRotateOneLayer();
        testCutOneLayer();
        testStackToTwoLayers();
        testStackToThreeLayers();
        testColor();
        System.out.println("ItemShapeTest: OK");
    }

    private static void testRotateOneLayer() {
        ItemShape shape = new ItemShape("CrOgFySp");

        shape.rotate();

        assertLayer("rotate layer one",
                shape.getSubShapes(ItemShape.Layer.one),
                shape.getColors(ItemShape.Layer.one),
                new SubShape[] { SubShape.Star, SubShape.Carre, SubShape.Circle, SubShape.Fan },
                new Color[] { Color.Purple, Color.Red, Color.Green, Color.Yellow });
    }

    private static void testCutOneLayer() {
        ItemShape shape = new ItemShape("CrOgFySp");

        ItemShape rightPart = shape.Cut();

        assertLayer("cut left kept in this",
                shape.getSubShapes(ItemShape.Layer.one),
                shape.getColors(ItemShape.Layer.one),
                new SubShape[] { SubShape.None, SubShape.None, SubShape.Fan, SubShape.Star },
                new Color[] { null, null, Color.Yellow, Color.Purple });

        assertLayer("cut right returned",
                rightPart.getSubShapes(ItemShape.Layer.one),
                rightPart.getColors(ItemShape.Layer.one),
                new SubShape[] { SubShape.Carre, SubShape.Circle, SubShape.None, SubShape.None },
                new Color[] { Color.Red, Color.Green, null, null });
    }

    private static void testStackToTwoLayers() {
        ItemShape base = new ItemShape("CrOr----");
        ItemShape top = new ItemShape("FySy----");

        base.stack(top);

        assertLayer("stack base layer",
                base.getSubShapes(ItemShape.Layer.one),
                base.getColors(ItemShape.Layer.one),
                new SubShape[] { SubShape.Carre, SubShape.Circle, SubShape.None, SubShape.None },
                new Color[] { Color.Red, Color.Red, null, null });

        assertLayer("stack second layer",
                base.getSubShapes(ItemShape.Layer.two),
                base.getColors(ItemShape.Layer.two),
                new SubShape[] { SubShape.Fan, SubShape.Star, SubShape.None, SubShape.None },
                new Color[] { Color.Yellow, Color.Yellow, null, null });
    }

    private static void testStackToThreeLayers() {
        ItemShape base = new ItemShape("Cr------Fy------");
        ItemShape top = new ItemShape("Og------Sp------");

        base.stack(top);

        assertLayer("stack keeps first layer",
                base.getSubShapes(ItemShape.Layer.one),
                base.getColors(ItemShape.Layer.one),
                new SubShape[] { SubShape.Carre, SubShape.None, SubShape.None, SubShape.None },
                new Color[] { Color.Red, null, null, null });

        assertLayer("stack keeps second layer",
                base.getSubShapes(ItemShape.Layer.two),
                base.getColors(ItemShape.Layer.two),
                new SubShape[] { SubShape.Fan, SubShape.None, SubShape.None, SubShape.None },
                new Color[] { Color.Yellow, null, null, null });

        assertLayer("stack adds third layer",
                base.getSubShapes(ItemShape.Layer.three),
                base.getColors(ItemShape.Layer.three),
                new SubShape[] { SubShape.Circle, SubShape.None, SubShape.None, SubShape.None },
                new Color[] { Color.Green, null, null, null });
    }

    private static void testColor() {
        ItemShape shape = new ItemShape("CrOg------------Sp------");

        shape.Color(Color.Blue);

        assertLayer("color recolors first layer",
                shape.getSubShapes(ItemShape.Layer.one),
                shape.getColors(ItemShape.Layer.one),
                new SubShape[] { SubShape.Carre, SubShape.Circle, SubShape.None, SubShape.None },
                new Color[] { Color.Blue, Color.Blue, null, null });

        assertLayer("color recolors third layer",
                shape.getSubShapes(ItemShape.Layer.three),
                shape.getColors(ItemShape.Layer.three),
                new SubShape[] { SubShape.Star, SubShape.None, SubShape.None, SubShape.None },
                new Color[] { Color.Blue, null, null, null });
    }

    private static void assertLayer(String testName, SubShape[] actualShapes, Color[] actualColors,
            SubShape[] expectedShapes, Color[] expectedColors) {
        for (int i = 0; i < expectedShapes.length; i++) {
            if (actualShapes[i] != expectedShapes[i]) {
                throw new AssertionError(testName + " shape mismatch at index " + i
                        + ": expected " + expectedShapes[i] + " but got " + actualShapes[i]);
            }
            if (actualColors[i] != expectedColors[i]) {
                throw new AssertionError(testName + " color mismatch at index " + i
                        + ": expected " + expectedColors[i] + " but got " + actualColors[i]);
            }
        }
    }
}
