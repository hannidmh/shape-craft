package modele.plateau;

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
}
