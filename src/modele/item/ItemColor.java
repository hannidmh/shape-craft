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

    }

}
