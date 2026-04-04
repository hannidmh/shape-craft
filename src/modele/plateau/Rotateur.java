package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class Rotateur extends Machine {
    private final Set<Item> processedItems = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public void work() {
        if (current.isEmpty()) {
            return;
        }

        Item item = current.getFirst();
        if (item instanceof ItemShape shape && !processedItems.contains(item)) {
            shape.rotate();
            processedItems.add(item);
        }
    }
}
