package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class Empileur extends Machine {
    private final Set<Item> processedItems = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public void work() {
        if (current.size() < 2) {
            return;
        }

        Item base = current.get(0);
        Item top = current.get(1);
        if (base instanceof ItemShape baseShape
                && top instanceof ItemShape topShape
                && !processedItems.contains(base)
                && !processedItems.contains(top)) {
            baseShape.stack(topShape);
            current.remove(1);
            processedItems.add(base);
        }
    }
}
