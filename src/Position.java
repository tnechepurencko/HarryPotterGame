public class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Position sum(Position position) {
        return new Position(this.x + position.x, this.y + position.y);
    }

    boolean correct() {
        return this.x > -1 && this.y < 9 && this.y > -1 && this.x < 9;
    }

    static boolean correct(int x, int y) {
        return x > -1 && y < 9 && y > -1 && x < 9;
    }
    /**
     * @param p: position to compare with
     * @return if current position and position "p" are the same.
     */
    boolean equals(Position p) {
        return this.x == p.x && this.y == p.y;
    }

    boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    /**
     * @return a copy of the current position.
     */
    Position copy() {
        return new Position(this.x, this.y);
    }
}
