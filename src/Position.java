public class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param position : position to add
     * @return : position where x = sum of the given abscissas & y = sum of the given ordinates
     */
    Position sum(Position position) {
        return new Position(this.x + position.x, this.y + position.y);
    }

    /**
     * @return if the current position is inside the field
     */
    boolean correct() {
        return this.x > -1 && this.y < 9 && this.y > -1 && this.x < 9;
    }

    /**
     * @param x : position.x
     * @param y : position.y
     * @return if the position is inside the field
     */
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

    /**
     * @param x : position "p".x
     * @param y : position "p".y
     * @return  if current position and position "p" are the same.
     */
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
