package modele.plateau;

import modele.item.Item;
import modele.item.ItemShape;

public class Coupeur extends Machine {
    private ItemShape leftOutput;
    private ItemShape rightOutput;

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
        if (hasPendingOutputs() || current.isEmpty()) {
            return;
        }

        Item input = current.getFirst();
        if (!(input instanceof ItemShape shape)) {
            return;
        }
        if (!canCut(shape)) {
            return;
        }

        current.removeFirst();

        // La forme d'entree est coupée et affectée aux deux sorties
        ItemShape producedSecond = shape.Cut();
        leftOutput = shape;
        rightOutput = producedSecond;
    }

    @Override
    public void send() {
        if (c == null) {
            return;
        }

        Case primaryOutput = c.plateau.getCase(c, d);
        Case secondaryCase = getSecondaryCase();
        Case secondaryOutput = secondaryCase == null ? null : c.plateau.getCase(secondaryCase, d);

        leftOutput = sendTo(primaryOutput, c, leftOutput);
        rightOutput = sendTo(secondaryOutput, secondaryCase, rightOutput);
    }

    @Override
    public Item getDisplayedItem(Case currentCase) {
        if (currentCase == null) {
            return null;
        }
        if (leftOutput != null || rightOutput != null) {
            return isPrimaryCase(currentCase) ? leftOutput : rightOutput;
        }
        if (current.isEmpty()) {
            return null;
        }
        return current.getFirst();
    }

    @Override
    public boolean isPrimaryCase(Case currentCase) {
        return c == currentCase;
    }

    @Override
    public void clearItems() {
        super.clearItems();
        leftOutput = null;
        rightOutput = null;
    }

    private boolean hasPendingOutputs() {
        return leftOutput != null || rightOutput != null;
    }

    private boolean canCut(ItemShape shape) {
        if (shape == null) {
            return false;
        }
        return switch (shape.getPart()) {
            case FULL, TOP, BOTTOM -> true;
            default -> false;
        };
    }

    private ItemShape sendTo(Case targetCase, Case sourceCase, ItemShape output) {
        if (output == null || targetCase == null || sourceCase == null) {
            return output;
        }

        Machine targetMachine = targetCase.getMachine();
        if (targetMachine == null) {
            return output;
        }

        return targetMachine.receive(output, targetCase, sourceCase) ? null : output;
    }

    private Case getSecondaryCase() {
        if (c == null) {
            return null;
        }
        return c.plateau.getCase(c, getSecondaryOffsetDirection());
    }

    /**
     * En orientation nord/sud, le cutter occupe deux cases horizontales.
     * En orientation est/ouest, il occupe deux cases verticales.
     */
    private Direction getSecondaryOffsetDirection() {
        return d.nextClockwise();
    }
}
