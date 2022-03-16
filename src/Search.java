import java.util.LinkedList;
import java.util.List;

public class Search {
    protected HarryPotter hp;
    protected int[][] BDFirstSearch;
    Field field;
    long runtime;
    int step;
    String result;
    LinkedList<Position> path;

    public Search(HarryPotter hp, Field field) {
        this.hp = hp;
        this.field = field;
        this.generateBDFirstSearch();
        this.path = new LinkedList<>();
        this.runtime = 0;
        this.step = 0;
        this.result = "LOSE";
    }

    boolean harryCaught() {
        return this.field.mrFilch.seeItem(this.hp.position) || this.field.mrsNorris.seeItem(this.hp.position);
    }

    /**
     * The method initializes "BFS" and fills it with zeros.
     */
    void generateBDFirstSearch() {
        this.BDFirstSearch = new int[9][9];
        this.updateBDFirstSearch();
    }

    void updateBDFirstSearch() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.BDFirstSearch[i][j] = 0;
            }
        }
    }

    protected boolean getItem() {
        if (!this.hp.hasBook && this.hp.position.equals(this.field.book.position)) {
            this.field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasBook = true;

            System.out.println("BOOK FOUND");
        }
        if (!this.hp.hasCloak && this.hp.position.equals(this.field.cloak.position)) {
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

    boolean cannotAccessBook() {
        if (this.hp.hasBook) {
            return false;
        } else {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (this.hp.memory[i][j].compareTo("·") == 0) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    boolean cannotAccessExit() {
        return this.hp.memory[this.hp.field.exit.x][this.hp.field.exit.y].compareTo("b") == 0;
    }

    public static List<Position> DELTAS = List.of(new Position(-1, 0), new Position(0, -1),
            new Position(1, 0), new Position(0, 1), new Position(1, 1), new Position(-1, -1),
            new Position(-1, 1), new Position(1, -1));

    void findBoundedArea(Position current) {
        this.BDFirstSearch[current.x][current.y] = 1;
        Position newPos;
        for (Position delta: DELTAS) {
            newPos = current.sum(delta);
            if (newPos.correct() && this.BDFirstSearch[newPos.x][newPos.y] == 0 && this.hp.notEnemy(current)) {
                this.findBoundedArea(newPos);
            }
        }
    }

    boolean boundedAreaExists() {
        this.updateBDFirstSearch();
        this.findBoundedArea(this.hp.position);
        boolean ans = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.BDFirstSearch[i][j] == 0 && this.hp.memory[i][j].compareTo("b") != 0) {
                    ans = true;
                    if (this.hp.notEnemy(i, j)) {
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
                    if (Position.correct(i, j)) {
                        if (this.hp.memory[i][j].compareTo("·") == 0) {
                            return new Position(i, j);
                        }
                    }
                }
            }
        }
        return new Position(-1, -1);
    }

    protected void checkAndPrint() {
        this.step++;
        if (!this.hp.norrisFound) {
            this.hp.checkNorris();
        }
        if (!this.hp.filchFound) {
            this.hp.checkFilch();
        }

        this.hp.updateMemory();
        if (this.boundedAreaExists()) {
            System.out.println("BOUNDED AREA FOUND");
        }

        System.out.println("Memory:");
        this.hp.printMemory();
    }
}
