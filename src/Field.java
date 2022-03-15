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

    void newGame() {
        this.hp.updateHarry();
        this.mrFilch.perception = 2;
        this.mrsNorris.perception = 1;
        this.update();
    }

    boolean inputCorrect() {
        // check Harry
        if (this.hp.position.x != 0 || this.hp.position.y != 0) {
            System.out.println("Position of Harry is entered incorrectly. The program will be terminated.");
            return false;
        }
        // inspectors are generated randomly
        // check exit
        if (this.exit.equals(this.mrFilch.position) || this.exit.equals(this.mrsNorris.position) ||
                this.exit.equals(this.book.position)) {
            System.out.println("Position of exit is entered incorrectly. The program will be terminated.");
            return false;
        }
        // check book & cloak
        if (this.mrFilch.seeItem(this.scheme) || this.mrsNorris.seeItem(this.scheme)) {
            System.out.println("Position of the book or the cloak is entered incorrectly. The program will be terminated.");
            return false;
        }

        return true;
    }

    // with input
    void generateActors(String[][] coords) {
        Position position = new Position(Integer.parseInt(coords[0][0]), Integer.parseInt(coords[0][1]));
        this.hp = new HarryPotter(this.scenario, position, this);

        position = new Position(Integer.parseInt(coords[1][0]), Integer.parseInt(coords[1][1]));
        this.mrFilch = new Inspector(2, position, "F");
        position = new Position(Integer.parseInt(coords[2][0]), Integer.parseInt(coords[2][1]));
        this.mrsNorris = new Inspector(1, position, "N");
    }

    // without input
    void generateActors() {
        Position position = new Position(0, 0);
        this.hp = new HarryPotter(this.scenario, position, this);

        position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        this.mrFilch = new Inspector(2, position, "F");
        position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        this.mrsNorris = new Inspector(1, position, "N");
    }

    // with input
    void generateItems(String[][] coords) {
        this.book = new Item(new Position(Integer.parseInt(coords[3][0]), Integer.parseInt(coords[3][1])));
        this.cloak = new Item(new Position(Integer.parseInt(coords[4][0]), Integer.parseInt(coords[4][1])));
    }

    // without input
    void generateItems() {
        Position position;

        do {
            position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        } while (this.mrsNorris.squarePerception(position, mrsNorris.perception) ||
                this.mrFilch.squarePerception(position, mrFilch.perception));
        this.book = new Item(position);

        do {
            position = new Position(random.nextInt(0, 9), random.nextInt(0, 9));
        } while (this.mrsNorris.squarePerception(position, mrsNorris.perception) ||
                this.mrFilch.squarePerception(position, mrFilch.perception));
        this.cloak = new Item(position);
    }

    // with input
    void generateExit(String[][] coords) {
        this.exit = new Position(Integer.parseInt(coords[5][0]), Integer.parseInt(coords[5][1]));
    }

    // without input
    void generateExit() {
        Position position;

        do {
            position = new Position(random.nextInt(0, 8), random.nextInt(0, 8));
        } while (this.mrsNorris.squarePerception(position, mrsNorris.perception) ||
                this.mrFilch.squarePerception(position, mrFilch.perception) || position.equals(this.book.position));

        this.exit = position;
    }

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

    void generateField() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                scheme[i][j] = "·";
            }
        }
    }

    void generatePerception(Inspector inspector) {
        for (int i = inspector.position.x - inspector.perception; i < inspector.position.x + inspector.perception+ 1; i++) {
            for (int j = inspector.position.y - inspector.perception; j < inspector.position.y + inspector.perception + 1; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9) {
                    this.scheme[i][j] = inspector.symbol.toLowerCase(Locale.ROOT);
                }
            }
        }
    }

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
