public class Person {
    Position position;
    String symbol;

    public Person(Position position, String symbol) {
        this.position = position;
        this.symbol = symbol;
    }

    /**
     * The method checks if there is an Item in the perception zone (not for Harry of 2nd scenario)
     * @param field: current field
     * @param perception: perception of the actor
     * @return if there is an Item in the perception zone.
     */
    boolean squarePerception(String[][] field, int perception) {
        for (int i = this.position.x - perception; i < this.position.x + perception + 1; i++) {
            for (int j = this.position.y - perception; j < this.position.y + perception + 1; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9) {
                    if (field[i][j].compareTo("B") == 0 || field[i][j].compareTo("C") == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * The method checks if the position in the perception zone (not for Harry of 2nd scenario)
     * @param position: position to check
     * @param perception: perception of the actor
     * @return if the position in the perception zone.
     */
    boolean squarePerception(Position position, int perception) {
        return position.x >= this.position.x - perception && position.x <= this.position.x + perception &&
                position.y >= this.position.y - perception && position.y <= this.position.y + perception;
    }
}
