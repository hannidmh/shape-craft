package modele.plateau;

import modele.item.Item;
import modele.item.ItemColor;

public class Mixeur extends Machine {
    private ItemColor pendingLeft;
    private ItemColor pendingRight;
    private ItemColor mixedOutput;

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
        if (mixedOutput != null || pendingLeft == null || pendingRight == null) {
            return;
        }

        ItemColor result = pendingLeft.copy();
        result.transform(pendingRight.getColor());
        mixedOutput = result;
        pendingLeft = null;
        pendingRight = null;
    }

    @Override
    public void send() {
        if (c == null || mixedOutput == null) {
            return;
        }

        Case outputCase = c.plateau.getCase(c, getOutputDirection());
        if (outputCase == null || outputCase.getMachine() == null) {
            return;
        }

        if (outputCase.getMachine().receive(mixedOutput, outputCase, c)) {
            mixedOutput = null;
        }
    }

    @Override
    public boolean receive(Item item, Case targetCase, Case sourceCase) {
        if (c == null || targetCase == null || sourceCase == null || mixedOutput != null) {
            return false;
        }

        Case secondaryCase = getSecondaryCase();

        if (targetCase == c
                && item instanceof ItemColor color
                && pendingLeft == null
                && c.plateau.getCase(sourceCase, getLeftInputDirection()) == c) {
            pendingLeft = color;
            return true;
        }

        if (secondaryCase != null
                && targetCase == secondaryCase
                && item instanceof ItemColor color
                && pendingRight == null
                && c.plateau.getCase(sourceCase, getRightInputDirection()) == secondaryCase) {
            pendingRight = color;
            return true;
        }

        return false;
    }

    @Override
    public Item getDisplayedItem(Case currentCase) {
        if (currentCase == null) {
            return null;
        }
        if (mixedOutput != null) {
            return currentCase == c ? mixedOutput : null;
        }
        if (currentCase == c) {
            return pendingLeft;
        }
        if (currentCase == getSecondaryCase()) {
            return pendingRight;
        }
        return null;
    }

    @Override
    public void clearItems() {
        super.clearItems();
        pendingLeft = null;
        pendingRight = null;
        mixedOutput = null;
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

    private Direction getLeftInputDirection() {
        return rotateRelative(Direction.North);
    }

    private Direction getRightInputDirection() {
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
