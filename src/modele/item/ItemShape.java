package modele.item;

/**
 * Représente une forme unique (non subdivisée) pouvant éventuellement
 * représenter la moitié gauche ou droite après une découpe.
 */
public class ItemShape extends Item {

    public enum ShapeType { CIRCLE, SQUARE, STAR, FAN }
    public enum Part { FULL, LEFT, TOP, RIGHT, BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    private final ShapeType type;
    private Color color;
    private Part part;
    private ItemShape stackedTop;

    public ItemShape(ShapeType type) {
        this(type, Color.Gray, Part.FULL);
    }

    public ItemShape(ShapeType type, Color color, Part part) {
        if (type == null || part == null) {
            throw new IllegalArgumentException("type et part sont obligatoires");
        }
        this.type = type;
        this.color = color == null ? Color.Gray : color;
        this.part = part;
    }

    public ShapeType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public Part getPart() {
        return part;
    }

    /**
     * Peint la shape si elle est matérielle (part != null).
     */
    public void Color(Color c) {
        if (c != null) {
            this.color = c;
            if (stackedTop != null) {
                stackedTop.Color(c);
            }
        }
    }

    /**
     * Découpe la forme en deux moitiés. L'instance courante devient la moitié
     * gauche et la moitié droite est renvoyée.
     */
    public ItemShape Cut() {
        switch (part) {
            case FULL -> {
                this.part = Part.LEFT;
                ItemShape rightHalf = new ItemShape(type, color, Part.RIGHT);
                if (stackedTop != null) {
                    rightHalf.stackedTop = stackedTop.Cut();
                }
                return rightHalf;
            }
            case TOP -> {
                this.part = Part.TOP_LEFT;
                ItemShape topRight = new ItemShape(type, color, Part.TOP_RIGHT);
                if (stackedTop != null) {
                    topRight.stackedTop = stackedTop.Cut();
                }
                return topRight;
            }
            case BOTTOM -> {
                this.part = Part.BOTTOM_LEFT;
                ItemShape bottomRight = new ItemShape(type, color, Part.BOTTOM_RIGHT);
                if (stackedTop != null) {
                    bottomRight.stackedTop = stackedTop.Cut();
                }
                return bottomRight;
            }
            default -> {
                return null; // déjà au quart, rien à couper
            }
        }
    }

    /**
     * Rotation 90° : pour les moitiés, on échange gauche/droite pour rester
     * cohérent visuellement. Les formes pleines sont inchangées.
     */
    public void rotate() {
        switch (part) {
            case LEFT -> part = Part.TOP;
            case TOP -> part = Part.RIGHT;
            case RIGHT -> part = Part.BOTTOM;
            case BOTTOM -> part = Part.LEFT;
            case TOP_LEFT -> part = Part.TOP_RIGHT;
            case TOP_RIGHT -> part = Part.BOTTOM_RIGHT;
            case BOTTOM_RIGHT -> part = Part.BOTTOM_LEFT;
            case BOTTOM_LEFT -> part = Part.TOP_LEFT;
            case FULL -> {
            }
        }
        if (stackedTop != null) {
            stackedTop.rotate();
        }
    }

    public ItemShape copy() {
        ItemShape copy = new ItemShape(type, color, part);
        if (stackedTop != null) {
            copy.stackedTop = stackedTop.copy();
        }
        return copy;
    }

    /**
     * Empilement : si les deux formes sont pleines et du même type, on conserve
     * la base et ignore le dessus (comportement minimal pour préserver API).
     */
    public void stack(ItemShape top) {
        if (top == null) {
            return;
        }
        if (stackedTop == null) {
            stackedTop = top.copy();
            return;
        }
        stackedTop.stack(top);
    }

    public ItemShape getStackedTop() {
        return stackedTop == null ? null : stackedTop.copy();
    }

    @Override
    public String toString() {
        if (stackedTop == null) {
            return type + ":" + part + ":" + color;
        }
        return type + ":" + part + ":" + color + "->" + stackedTop;
    }
}
