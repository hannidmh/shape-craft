package modele.plateau;

import modele.item.Item;
import modele.item.ItemColor;
import modele.item.ItemShape;

public class Mine extends Machine {

    @Override
    public void work() {
        if (!current.isEmpty() || c == null || c.getGisement() == null) {
            return;
        }
        Item producedItem = duplicateItem(c.getGisement());
        if (producedItem != null) {
            current.add(producedItem);
        }
    }

    @Override
    public void send() {
        super.send();
    }

    private Item duplicateItem(Item source) {
        if (source instanceof ItemShape shape) {
            // Toujours générer une forme pleine et grise du type du gisement
            return new ItemShape(shape.getType());
        }
        if (source instanceof ItemColor itemColor) {
            return itemColor.copy();
        }
        return null;
    }
}
