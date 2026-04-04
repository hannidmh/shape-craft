package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class Coupeur extends Machine {
    private final Set<Item> processedItems = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public void work() {
        if (current.isEmpty()) {
            return;
        }

        Item item = current.getFirst();
        if (item instanceof ItemShape shape && !processedItems.contains(item)) {
            ItemShape secondHalf = shape.Cut();
            processedItems.add(item);
            if (secondHalf != null) {
                current.add(1, secondHalf);
                processedItems.add(secondHalf);
            }
        }
    }

    @Override
    public void send() {
        if (current.isEmpty()) {
            return;
        }

        Direction leftDirection = turnLeft(d);
        Direction rightDirection = turnRight(d);

        Case frontCase = c.plateau.getCase(c, d);
        if (frontCase == null) {
            return;
        }

        Case frontLeftCase = c.plateau.getCase(frontCase, leftDirection);
        Case frontRightCase = c.plateau.getCase(frontCase, rightDirection);

        sendHalfTo(frontLeftCase, 0);
        sendHalfTo(frontRightCase, 1);
    }

    private void sendHalfTo(Case targetCase, int itemIndex) {
        if (targetCase == null || current.size() <= itemIndex) {
            return;
        }

        Machine targetMachine = targetCase.getMachine();
        if (targetMachine == null) {
            return;
        }

        Item item = current.get(itemIndex);
        targetMachine.incoming.add(item);
        current.remove(itemIndex);
    }

    private Direction turnLeft(Direction direction) {
        return switch (direction) {
            case North -> Direction.West;
            case South -> Direction.East;
            case East -> Direction.North;
            case West -> Direction.South;
        };
    }

    private Direction turnRight(Direction direction) {
        return switch (direction) {
            case North -> Direction.East;
            case South -> Direction.West;
            case East -> Direction.South;
            case West -> Direction.North;
        };
    }

    public boolean isPrimaryCase(Case currentCase) {
        return c == currentCase;
    }
}
