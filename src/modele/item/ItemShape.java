package modele.item;

/**
 * Représente une forme unique (non subdivisée) pouvant éventuellement
 * représenter la moitié gauche ou droite après une découpe.
 */
public class ItemShape extends Item {

    public enum ShapeType { CIRCLE, SQUARE, STAR, FAN }
    public enum Part { FULL, LEFT, RIGHT }

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
        if (part != Part.FULL) {
            return null; // déjà découpée, on ignore
        }
        this.part = Part.LEFT;
        ItemShape rightHalf = new ItemShape(type, color, Part.RIGHT);
        if (stackedTop != null) {
            rightHalf.stackedTop = stackedTop.Cut();
        }
        return rightHalf;
    }

    /**
     * Rotation 90° : pour les moitiés, on échange gauche/droite pour rester
     * cohérent visuellement. Les formes pleines sont inchangées.
     */
    public void rotate() {
        if (part == Part.LEFT) {
            part = Part.RIGHT;
        } else if (part == Part.RIGHT) {
            part = Part.LEFT;
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
