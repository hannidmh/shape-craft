package modele.item;

public class ItemColor extends Item {
    Color color;

    public ItemColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public ItemColor copy() {
        return new ItemColor(color);
    }

    public void transform(Color add) { // faire varier la couleur suivant la couleur ajoutée
        if (add == null) {
            return;
        }
        color = fromMask(toMask(color) | toMask(add));
    }

    private int toMask(Color color) {
        if (color == null) {
            return 0;
        }
        return switch (color) {
            case Gray -> 0;
            case Red -> 1;
            case Green -> 2;
            case Yellow -> 3;
            case Blue -> 4;
            case Purple -> 5;
            case Cyan -> 6;
            case White -> 7;
        };
    }

    private Color fromMask(int mask) {
        return switch (mask) {
            case 1 -> Color.Red;
            case 2 -> Color.Green;
            case 3 -> Color.Yellow;
            case 4 -> Color.Blue;
            case 5 -> Color.Purple;
            case 6 -> Color.Cyan;
            case 7 -> Color.White;
            default -> Color.Gray;
        };
    }

}
