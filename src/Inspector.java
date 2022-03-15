public class Inspector extends Person {
    int perception;

    public Inspector(int perception, Position position, String symbol) {
        super(position, symbol);
        this.perception = perception;
    }

    /**
     * @param position: position to check
     * @return if the position in the perception zone of the inspector.
     */
    boolean seeItem(Position position) {
        return position.x >= this.position.x - perception && position.x <= this.position.x + perception &&
                position.y >= this.position.y - perception && position.y <= this.position.y + perception;
    }
}
