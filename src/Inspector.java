public class Inspector extends Person {
    int currentPerception;
    int staticPerception;

    public Inspector(int perception, Position position, String symbol) {
        super(position, symbol);
        this.staticPerception = perception;
        this.currentPerception = perception;
    }

    /**
     * @param position: position to check
     * @return if the position in the perception zone of the inspector.
     */
    boolean seeItem(Position position) {
        return position.x >= this.position.x - currentPerception && position.x <= this.position.x + currentPerception &&
                position.y >= this.position.y - currentPerception && position.y <= this.position.y + currentPerception;
    }
}
