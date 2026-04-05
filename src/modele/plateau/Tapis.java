package modele.plateau;

import modele.item.Item;

public class Tapis extends Machine {
    private Direction incomingDirection;

    @Override
    public void work() {

    }

    public Direction getIncomingDirection() {
        return incomingDirection;
    }

    public void setIncomingDirection(Direction incomingDirection) {
        this.incomingDirection = incomingDirection;
    }

    @Override
    public boolean receive(Item item, Case targetCase, Case sourceCase) {
        if (c == null || targetCase != c || sourceCase == null) {
            return false;
        }

        Direction expectedIncomingDirection = incomingDirection == null ? opposite(d) : incomingDirection;
        Case expectedSourceCase = c.plateau.getCase(c, expectedIncomingDirection);
        if (expectedSourceCase != sourceCase) {
            return false;
        }

        return super.receive(item, targetCase, sourceCase);
    }

    @Override
    public void rotateClockwise() {
        super.rotateClockwise();
        if (incomingDirection != null) {
            incomingDirection = incomingDirection.nextClockwise();
        }
    }

    private Direction opposite(Direction direction) {
        return switch (direction) {
            case North -> Direction.South;
            case South -> Direction.North;
            case East -> Direction.West;
            case West -> Direction.East;
        };
    }
}
