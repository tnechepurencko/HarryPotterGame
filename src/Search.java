import java.util.List;
import java.util.concurrent.TimeUnit;

public class Search {
    protected HarryPotter hp;
    int[][] DFS;
    Field field;

    public Search(HarryPotter hp, Field field) {
        this.hp = hp;
        this.field = field;
        this.generateDFS();
    }

    /**
     * The method initializes "BFS" and fills it with zeros.
     */
    void generateDFS() {
        this.DFS = new int[9][9];
        this.updateDFS();
    }

    void updateDFS() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.DFS[i][j] = 0;
            }
        }
    }

    protected boolean getItem() {
        if (this.hp.position.equals(this.field.book.position)) {
            this.field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasBook = true;
            System.out.println("BOOK FOUND");
        }
        if (this.hp.position.equals(this.field.cloak.position)) {
            this.field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasCloak = true;
            System.out.println("CLOAK FOUND");

            this.field.mrFilch.perception = 0;
            this.field.mrsNorris.perception = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (this.field.scheme[i][j].compareTo("n") == 0 || this.field.scheme[i][j].compareTo("f") == 0) {
                        this.field.scheme[i][j] = "·";
                        this.hp.memory[i][j] = "·";
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    boolean cannotGetBook() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.hp.memory[i][j].compareTo("·") == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<Position> DELTAS = List.of(new Position(-1, 0), new Position(0, -1),
            new Position(1, 0), new Position(0, 1), new Position(1, 1), new Position(-1, -1),
            new Position(-1, 1), new Position(1, -1));

    void findBoundedArea(Position current) {
        String symbol = this.hp.memory[current.x][current.y];
        if (symbol.compareTo("F") == 0 || symbol.compareTo("N") == 0 ||
                symbol.compareTo("f") == 0 || symbol.compareTo("n") == 0) {
            this.DFS[current.x][current.y] = 1;
        } else {
            this.DFS[current.x][current.y] = 1;
            for (Position delta: DELTAS) {
                Position newPos = current.sum(delta);
                if (newPos.positionCorrect() && this.DFS[newPos.x][newPos.y] == 0) {
                    this.findBoundedArea(newPos);
                }
            }
        }
    }

    boolean boundedAreaExists() {
        this.updateDFS();
        this.findBoundedArea(this.hp.position);
        boolean ans = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.DFS[i][j] == 0 && this.hp.memory[i][j].compareTo("b") != 0) {
                    ans = true;
                    if (this.hp.memory[i][j].compareTo("F") != 0 || this.hp.memory[i][j].compareTo("N") != 0 ||
                            this.hp.memory[i][j].compareTo("f") != 0 || this.hp.memory[i][j].compareTo("n") != 0) {
                        this.hp.memory[i][j] = "b";
                    }
                }
            }
        }
        return ans;
    }

    protected Position closestUnknown() {
        for (int radius = 1; radius < 8; radius++) {
            for (int i = this.hp.position.x - radius; i < this.hp.position.x + radius + 1; i++) {
                for (int j = this.hp.position.y - radius; j < this.hp.position.y + radius + 1; j++) {
                    if (i > -1 && i < 9 && j > -1 && j < 9) {
                        if (this.hp.memory[i][j].compareTo("·") == 0) {
                            return new Position(i, j);
                        }
                    }
                }
            }
        }
        return new Position(-1, -1);
    }

    protected void checkAndPrint(Field field, int stepNumber) {
        if (!this.hp.norrisFound) {
            this.hp.checkNorris(field);
        }
        if (!this.hp.filchFound) {
            this.hp.checkFilch(field);
        }

        if (this.boundedAreaExists()) {
            System.out.println("BOUNDED AREA FOUND");
        }

        this.hp.updateMemory(field);
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("STEP " + stepNumber);
        System.out.println("Memory:");
        this.hp.printMemory();
    }
}
