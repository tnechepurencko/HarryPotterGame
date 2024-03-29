import java.util.Locale;
import java.util.Random;

public class Field {
    Random random = new Random();

    String[][] scheme;
    int scenario;

    HarryPotter hp;
    Inspector mrFilch;
    Inspector mrsNorris;
    Item book;
    Item cloak;
    Position exit;

    // with input
    public Field(String[][] coords, int scenario) {
        this.scenario = scenario;
        this.scheme = new String[9][9];

        generateActors(coords);
        generateItems(coords);
        generateExit(coords);

        this.update();
        this.hp.prepareHarry();
    }

    // without input
    public Field(int scenario) {
        this.scenario = scenario;
        this.scheme = new String[9][9];

        generateActors();
        generateItems();
        generateExit();

        this.update();
        this.hp.prepareHarry();
    }

    /**
     * The method resets the field
     */
    void newGame() {
        this.hp.updateHarry();
        this.mrFilch.currentPerception = 2;
        this.mrsNorris.currentPerception = 1;
        this.update();
    }

    /**
     * The method marks the actors on the field.
     * @param coords : input coordinates
     */
    // with input
    void generateActors(String[][] coords) {
        Position position = new Position(Integer.parseInt(coords[0][0]), Integer.parseInt(coords[0][1]));
        this.hp = new HarryPotter(this.scenario, position, this);

        position = new Position(Integer.parseInt(coords[1][0]), Integer.parseInt(coords[1][1]));
        this.mrFilch = new Inspector(2, position, "F");
        position = new Position(Integer.parseInt(coords[2][0]), Integer.parseInt(coords[2][1]));
        this.mrsNorris = new Inspector(1, position, "N");
    }

    /**
     * The method generates actors in random (except Harry) & marks them on the field.
     */
    // without input
    void generateActors() {
        Position position = new Position(0, 0);
        this.hp = new HarryPotter(this.scenario, position, this);

        position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        this.mrFilch = new Inspector(2, position, "F");
        position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        this.mrsNorris = new Inspector(1, position, "N");
    }

    /**
     * The method marks the book & the cloak on the field.
     * @param coords : input coordinates
     */
    // with input
    void generateItems(String[][] coords) {
        this.book = new Item(new Position(Integer.parseInt(coords[3][0]), Integer.parseInt(coords[3][1])));
        this.cloak = new Item(new Position(Integer.parseInt(coords[4][0]), Integer.parseInt(coords[4][1])));
    }

    /**
     * The method generates the book & the cloak in random & marks them on the field.
     */
    // without input
    void generateItems() {
        Position position;

        do {
            position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        } while (this.mrsNorris.seeItem(position) || this.mrFilch.seeItem(position));
        this.book = new Item(position);

        do {
            position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        } while (this.mrsNorris.seeItem(position) || this.mrFilch.seeItem(position));
        this.cloak = new Item(position);
    }

    /**
     * The method marks exit on the field.
     * @param coords : input coordinates
     */
    // with input
    void generateExit(String[][] coords) {
        this.exit = new Position(Integer.parseInt(coords[5][0]), Integer.parseInt(coords[5][1]));
    }

    /**
     * The method generates exit in random & marks exit on the field.
     */
    // without input
    void generateExit() {
        Position position;

        do {
            position = new Position(random.nextInt(0, 8), random.nextInt(0, 8));
        } while (this.mrsNorris.seeItem(position) || this.mrFilch.seeItem(position) || position.equals(this.book.position));

        this.exit = position;
    }

    /**
     * The method resets the field.
     */
    void update() {
        this.generateField();
        this.generatePerception(mrFilch);
        this.generatePerception(mrsNorris);

        scheme[this.mrFilch.position.x][this.mrFilch.position.y] = "F";
        scheme[this.mrsNorris.position.x][this.mrsNorris.position.y] = "N";
        scheme[this.hp.position.x][this.hp.position.y] = "H";

        scheme[this.book.position.x][this.book.position.y] = "B";
        scheme[this.cloak.position.x][this.cloak.position.y] = "C";

        scheme[this.exit.x][this.exit.y] = "E";
    }

    /**
     * The method fills the field with "·".
     */
    void generateField() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                scheme[i][j] = "·";
            }
        }
    }

    /**
     * The method marks cells that are under the perception of the inspector.
     * @param inspector : the inspector
     */
    void generatePerception(Inspector inspector) {
        for (int i = inspector.position.x - inspector.currentPerception; i < inspector.position.x + inspector.currentPerception + 1; i++) {
            for (int j = inspector.position.y - inspector.currentPerception; j < inspector.position.y + inspector.currentPerception + 1; j++) {
                if (Position.correct(i, j)) {
                    this.scheme[i][j] = inspector.symbol.toLowerCase(Locale.ROOT);
                }
            }
        }
    }

    /**
     * @param position : position to check
     * @return if there is no enemy on this position
     */
    boolean notEnemy(Position position) {
        return this.scheme[position.x][position.y].compareTo("F") != 0 &&
                this.scheme[position.x][position.y].compareTo("N") != 0 &&
                this.scheme[position.x][position.y].compareTo("f") != 0 &&
                this.scheme[position.x][position.y].compareTo("n") != 0;
    }

    /**
     * @param x : position.x
     * @param y : position.y
     * @return if there is no enemy on this position
     */
    boolean notEnemy(int x, int y) {
        return this.scheme[x][y].compareTo("F") != 0 && this.scheme[x][y].compareTo("N") != 0 &&
                this.scheme[x][y].compareTo("f") != 0 && this.scheme[x][y].compareTo("n") != 0;
    }

    /**
     * The method prints the field.
     */
    void print() {
        this.update();
        for (int i = 8; i > -1; i--) {
            for (int j = 0; j < 9; j++) {
                System.out.print(scheme[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
