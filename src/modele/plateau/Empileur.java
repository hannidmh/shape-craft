package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class Empileur extends Machine {
    private ItemShape pendingBottom;
    private ItemShape pendingTop;
    private ItemShape stackedOutput;

    @Override
    public Point[] getFootprint(Direction direction) {
        Direction secondaryOffsetDirection = direction.nextClockwise();
        return new Point[] {
                new Point(0, 0),
                new Point(secondaryOffsetDirection.dx, secondaryOffsetDirection.dy)
        };
    }

    @Override
    public void work() {
        if (stackedOutput != null || pendingBottom == null || pendingTop == null) {
            return;
        }

        pendingBottom.stack(pendingTop);
        stackedOutput = pendingBottom;
        pendingBottom = null;
        pendingTop = null;
    }

    @Override
    public void send() {
        if (c == null || stackedOutput == null) {
            return;
        }

        Case outputCase = c.plateau.getCase(c, getOutputDirection());
        if (outputCase == null || outputCase.getMachine() == null) {
            return;
        }

        if (outputCase.getMachine().receive(stackedOutput, outputCase, c)) {
            stackedOutput = null;
        }
    }

    @Override
    public boolean receive(Item item, Case targetCase, Case sourceCase) {
        if (c == null || targetCase == null || sourceCase == null || stackedOutput != null) {
            return false;
        }

        Case secondaryCase = getSecondaryCase();

        if (targetCase == c
                && item instanceof ItemShape shape
                && pendingBottom == null
                && c.plateau.getCase(sourceCase, getBottomInputDirection()) == c) {
            pendingBottom = shape;
            return true;
        }

        if (secondaryCase != null
                && targetCase == secondaryCase
                && item instanceof ItemShape shape
                && pendingTop == null
                && c.plateau.getCase(sourceCase, getTopInputDirection()) == secondaryCase) {
            pendingTop = shape;
            return true;
        }

        return false;
    }

    @Override
    public Item getDisplayedItem(Case currentCase) {
        if (currentCase == null) {
            return null;
        }
        if (stackedOutput != null) {
            return currentCase == c ? stackedOutput : null;
        }
        if (currentCase == c) {
            return pendingBottom;
        }
        if (currentCase == getSecondaryCase()) {
            return pendingTop;
        }
        return null;
    }

    @Override
    public void clearItems() {
        super.clearItems();
        pendingBottom = null;
        pendingTop = null;
        stackedOutput = null;
    }

    private Case getSecondaryCase() {
        if (c == null) {
            return null;
        }
        return c.plateau.getCase(c, getSecondaryOffsetDirection());
    }

    private Direction getSecondaryOffsetDirection() {
        return d.nextClockwise();
    }

    private Direction getBottomInputDirection() {
        return rotateRelative(Direction.North);
    }

    private Direction getTopInputDirection() {
        return rotateRelative(Direction.North);
    }

    private Direction getOutputDirection() {
        return rotateRelative(Direction.North);
    }

    private Direction rotateRelative(Direction absoluteDirectionWhenFacingNorth) {
        Direction rotatedDirection = absoluteDirectionWhenFacingNorth;
        Direction currentOrientation = Direction.North;
        while (currentOrientation != d) {
            rotatedDirection = rotatedDirection.nextClockwise();
            currentOrientation = currentOrientation.nextClockwise();
        }
        return rotatedDirection;
    }
}
