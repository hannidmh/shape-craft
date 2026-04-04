package modele.item;

public class ItemShape extends Item {
    private static final int SUBSHAPES_PER_LAYER = 4;
    private static final int MAX_LAYERS = 3;
    private static final int TOTAL_SUBSHAPES = SUBSHAPES_PER_LAYER * MAX_LAYERS;

    private SubShape[] tabSubShapes;
    private Color[] tabColors;

    public enum Layer {
        one, two, three
    };

    public SubShape[] getSubShapes(Layer l) {
        switch (l) {
            case one:
                return new SubShape[] { tabSubShapes[0], tabSubShapes[1], tabSubShapes[2], tabSubShapes[3] };

            case two :
                return  new SubShape[] {tabSubShapes[4],tabSubShapes[5],tabSubShapes[6],tabSubShapes[7]};

            case three :
                return new SubShape[]{tabSubShapes[8],tabSubShapes[9],tabSubShapes[10],tabSubShapes[11]};
            default:
                throw new IllegalStateException("Unexpected value: " + l);
        }
    }

    public Color[] getColors(Layer l) {
        return switch (l) {
            case one -> new Color[]{tabColors[0], tabColors[1], tabColors[2], tabColors[3]};
            case two -> new Color[]{tabColors[4], tabColors[5], tabColors[6], tabColors[7]};
            case three -> new Color[]{tabColors[8], tabColors[9], tabColors[10], tabColors[11]};
            default -> throw new IllegalStateException("Unexpected value: " + l);
        };
    }

    /**
     * ;
     * Initialisation des formes par chaîne de caractères
     * 
     * @param str : codage : (sous forme + couleur ) * (haut-droit, bas-droit,
     *            bas-gauche, haut-gauche) * 3 Layers
     *            str.length multiple de 4
     */
    public ItemShape(String str) {
        if (str == null) {
            throw new IllegalArgumentException("La chaîne d'initialisation ne peut pas être nulle");
        }

        if (str.length() % 2 != 0) {
            throw new IllegalArgumentException("La chaîne doit contenir un nombre pair de caractères");
        }

        int nbSubShapes = str.length() / 2;
        if (nbSubShapes % 4 != 0) {
            throw new IllegalArgumentException("Le nombre de sous-formes doit être un multiple de 4");
        }

        int nbLayers = nbSubShapes / 4;
        if (nbLayers < 1 || nbLayers > 3) {
            throw new IllegalArgumentException("Le nombre de layers doit être compris entre 1 et 3");
        }

        tabSubShapes = new SubShape[TOTAL_SUBSHAPES];
        tabColors = new Color[TOTAL_SUBSHAPES];
        fillEmpty(tabSubShapes, tabColors);

        for (int i = 0; i < nbSubShapes; i++) {
            tabSubShapes[i] = parseSubShape(str.charAt(i * 2));
            tabColors[i] = parseColor(str.charAt(i * 2 + 1));
        }
    }

    private ItemShape(SubShape[] subShapes, Color[] colors) {
        tabSubShapes = subShapes.clone();
        tabColors = colors.clone();
    }

    public ItemShape copy() {
        return new ItemShape(tabSubShapes, tabColors);
    }



    // les paramètres éventuels (sens, axe, etc.)
    public void rotate() {
        SubShape[] rotatedSubShapes = new SubShape[TOTAL_SUBSHAPES];
        Color[] rotatedColors = new Color[TOTAL_SUBSHAPES];
        fillEmpty(rotatedSubShapes, rotatedColors);

        for (int layer = 0; layer < MAX_LAYERS; layer++) {
            int start = getLayerStart(layer);
            rotatedSubShapes[start] = tabSubShapes[start + 3];
            rotatedSubShapes[start + 1] = tabSubShapes[start];
            rotatedSubShapes[start + 2] = tabSubShapes[start + 1];
            rotatedSubShapes[start + 3] = tabSubShapes[start + 2];

            rotatedColors[start] = tabColors[start + 3];
            rotatedColors[start + 1] = tabColors[start];
            rotatedColors[start + 2] = tabColors[start + 1];
            rotatedColors[start + 3] = tabColors[start + 2];
        }

        tabSubShapes = rotatedSubShapes;
        tabColors = rotatedColors;
    }

    public void stack(ItemShape shapeSup) { // shapeSup est empilé sur this
        if (shapeSup == null) {
            return;
        }

        SubShape[] stackedSub = new SubShape[TOTAL_SUBSHAPES];
        Color[] stackedCol = new Color[TOTAL_SUBSHAPES];
        fillEmpty(stackedSub, stackedCol);

        int targetLayer = 0;
        targetLayer = appendNonEmptyLayers(stackedSub, stackedCol, targetLayer, this.tabSubShapes, this.tabColors);
        appendNonEmptyLayers(stackedSub, stackedCol, targetLayer, shapeSup.tabSubShapes, shapeSup.tabColors);

        tabSubShapes = stackedSub;
        tabColors = stackedCol;
    }

    public ItemShape Cut() {
        SubShape[] leftSub = new SubShape[TOTAL_SUBSHAPES];
        Color[] leftCol = new Color[TOTAL_SUBSHAPES];
        SubShape[] rightSub = new SubShape[TOTAL_SUBSHAPES];
        Color[] rightCol = new Color[TOTAL_SUBSHAPES];
        fillEmpty(leftSub, leftCol);
        fillEmpty(rightSub, rightCol);

        for (int layer = 0; layer < MAX_LAYERS; layer++) {
            int start = getLayerStart(layer);

            leftSub[start + 2] = tabSubShapes[start + 2];
            leftSub[start + 3] = tabSubShapes[start + 3];
            leftCol[start + 2] = tabColors[start + 2];
            leftCol[start + 3] = tabColors[start + 3];

            rightSub[start] = tabSubShapes[start];
            rightSub[start + 1] = tabSubShapes[start + 1];
            rightCol[start] = tabColors[start];
            rightCol[start + 1] = tabColors[start + 1];
        }

        tabSubShapes = leftSub;
        tabColors = leftCol;
        return new ItemShape(rightSub, rightCol);
    }

    public void Color(Color c) {
        if (c == null) {
            return;
        }

        for (int i = 0; i < TOTAL_SUBSHAPES; i++) {
            if (tabSubShapes[i] != SubShape.None) {
                tabColors[i] = c;
            }
        }
    }

    private static void fillEmpty(SubShape[] subShapes, Color[] colors) {
        for (int i = 0; i < TOTAL_SUBSHAPES; i++) {
            subShapes[i] = SubShape.None;
            colors[i] = null;
        }
    }

    private static int getLayerStart(int layerIndex) {
        return layerIndex * SUBSHAPES_PER_LAYER;
    }

    private static int appendNonEmptyLayers(SubShape[] destinationSubShapes, Color[] destinationColors,
            int targetLayer, SubShape[] sourceSubShapes, Color[] sourceColors) {
        for (int layer = 0; layer < MAX_LAYERS && targetLayer < MAX_LAYERS; layer++) {
            if (isLayerEmpty(sourceSubShapes, layer)) {
                continue;
            }
            copyLayer(sourceSubShapes, sourceColors, layer, destinationSubShapes, destinationColors, targetLayer);
            targetLayer++;
        }
        return targetLayer;
    }

    private static boolean isLayerEmpty(SubShape[] subShapes, int layer) {
        int start = getLayerStart(layer);
        for (int i = 0; i < SUBSHAPES_PER_LAYER; i++) {
            if (subShapes[start + i] != SubShape.None) {
                return false;
            }
        }
        return true;
    }

    private static void copyLayer(SubShape[] sourceSubShapes, Color[] sourceColors, int sourceLayer,
            SubShape[] destinationSubShapes, Color[] destinationColors, int destinationLayer) {
        int sourceStart = getLayerStart(sourceLayer);
        int destinationStart = getLayerStart(destinationLayer);

        for (int i = 0; i < SUBSHAPES_PER_LAYER; i++) {
            destinationSubShapes[destinationStart + i] = sourceSubShapes[sourceStart + i];
            destinationColors[destinationStart + i] = sourceColors[sourceStart + i];
        }
    }

    private static SubShape parseSubShape(char shapeCode) {
        return switch (shapeCode) {
            case 'C' -> SubShape.Carre;
            case 'O' -> SubShape.Circle;
            case 'F' -> SubShape.Fan;
            case 'S' -> SubShape.Star;
            case '-' -> SubShape.None;
            default -> throw new IllegalArgumentException("Sous-forme inconnue: " + shapeCode);
        };
    }

    private static Color parseColor(char colorCode) {
        return switch (colorCode) {
            case 'r' -> Color.Red;
            case 'g' -> Color.Green;
            case 'u' -> Color.Blue;
            case 'y' -> Color.Yellow;
            case 'p' -> Color.Purple;
            case 'c' -> Color.Cyan;
            case 'w', 'b' -> Color.White;
            case '-' -> null;
            default -> throw new IllegalArgumentException("Couleur inconnue: " + colorCode);
        };
    }

}
