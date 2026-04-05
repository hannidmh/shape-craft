package modele.plateau;

public enum Direction {
    North(0, -1), South(0, 1), East(1, 0), West(-1, 0);

    int dx;
    int dy;
    private Direction(int _dx, int _dy) {
        dx = _dx;
        dy = _dy;
    }

    public Direction nextClockwise() {
        return switch (this) {
            case North -> East;
            case East -> South;
            case South -> West;
            case West -> North;
        };
    }
}
