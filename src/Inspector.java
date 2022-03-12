public class Inspector extends Person {
    int perception;

    public Inspector(int perception, Position position, String symbol) {
        super(position, symbol);
        this.perception = perception;
    }

    /**
     * @param field: current field
     * @return if an Item in the perception zone of the inspector.
     */
    boolean seeItem(String[][] field) {
        return this.squarePerception(field, this.perception);
    }
}
