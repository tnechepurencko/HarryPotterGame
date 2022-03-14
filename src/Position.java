public class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Position sum(Position delta) {
        return new Position(this.x + delta.x, this.y + delta.y);
    }

    boolean positionCorrect() {
        return this.x > -1 && this.y < 9 && this.y > -1 && this.x < 9;
    }

    /**
     * @param p: position to compare with
     * @return if current position and position "p" are the same.
     */
    boolean equals(Position p) {
        return this.x == p.x && this.y == p.y;
    }

    /**
     * @param p: position to compare with
     * @return if current position is upper then position "p".
     */
    boolean upper(Position p) {
        return this.x > p.x;
    }

    /**
     * @param p: position to compare with
     * @return if current position is downer then position "p".
     */
    boolean downer(Position p) {
        return this.x < p.x;
    }

    /**
     * @param p: position to compare with
     * @return if current position is leftward then position "p".
     */
    boolean leftward(Position p) {
        return this.y < p.y;
    }

    /**
     * @param p: position to compare with
     * @return if current position is rightward then position "p".
     */
    boolean rightward(Position p) {
        return this.y > p.y;
    }

    /**
     * @return a copy of the current position.
     */
    Position copy() {
        return new Position(this.x, this.y);
    }
}
