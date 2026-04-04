package modele.plateau;

import modele.item.Color;
import modele.item.Item;
import modele.item.ItemShape;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class Peintre extends Machine {
    private final Set<Item> processedItems = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Color paintColor;

    public Peintre() {
        this(Color.Blue);
    }

    public Peintre(Color paintColor) {
        this.paintColor = paintColor;
    }

    @Override
    public void work() {
        if (current.isEmpty()) {
            return;
        }

        Item item = current.getFirst();
        if (item instanceof ItemShape shape && !processedItems.contains(item)) {
            shape.Color(paintColor);
            processedItems.add(item);
        }
    }
}
