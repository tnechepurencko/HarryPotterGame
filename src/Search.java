import java.util.LinkedList;
import java.util.List;

public class Search {
    protected HarryPotter hp;
    protected int[][] BDFirstSearch;
    protected int[][] fear; // 1-enemy possible, 2-no enemy, 0-unknown
    Field field;
    long runtime;
    int step;
    String result;
    LinkedList<Position> path;

    public Search(HarryPotter hp, Field field) {
        this.hp = hp;
        this.field = field;
        this.BDFirstSearch = new int[9][9];
        this.generateBDFirstSearch();

        this.fear = new int[9][9];
        this.generateFear();

        this.path = new LinkedList<>();
        this.runtime = 0;
        this.step = 0;
        this.result = "LOSE";
    }

    boolean harryCaught() {
        return this.field.mrFilch.seeItem(this.hp.position) || this.field.mrsNorris.seeItem(this.hp.position);
    }

    /**
     * The method fills "BDFirstSearch" with zeros.
     */
    void generateBDFirstSearch() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.BDFirstSearch[i][j] = 0;
            }
        }
    }

    /**
     * The method fills "fear" with zeros.
     */
    void generateFear() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.fear[i][j] != 2) {
                    this.fear[i][j] = 0;
                }
            }
        }
    }

    /**
     * Fear parameters:
     * if fear[i][j] == 0: position is not checked
     * if fear[i][j] == 1: an enemy can be on this position
     * if fear[i][j] == 2: no enemy can be on this position
     * @param current : current position
     */
    void updateFear(Position current) {
        if (this.hp.scenario == 2) {
            List<Position> deltas = List.of(new Position(-1, 0), new Position(0, -1),
                    new Position(1, 0), new Position(0, 1));
            Position maybeInspector;
            Position near;
            for (Position delta0 : DELTAS) {
                near = current.sum(delta0);
                if (near.correct()) {
                    for (Position delta1 : deltas) {
                        maybeInspector = near.sum(delta1);
                        if (maybeInspector.correct() && !maybeInspector.equals(current) &&
                                this.fear[near.x][near.y] != 2 &&
                                (this.hp.memory[maybeInspector.x][maybeInspector.y].compareTo("f") == 0 ||
                                this.hp.memory[maybeInspector.x][maybeInspector.y].compareTo("n") == 0)) {
                            this.fear[near.x][near.y] = 1;
                            break;
                        }
                    }
                }
            }

            int i, j;
            for (i = current.x - 1; i <current.x + 2; i++) {
                j = current.y - 2;
                if (Position.correct(i, j) && this.field.notEnemy(i, j)) {
                    this.fear[i][j] = 2;
                }

                j = current.y + 2;
                if (Position.correct(i, j) && this.field.notEnemy(i, j)) {
                    this.fear[i][j] = 2;
                }
            }

            for (j = current.y - 1; j < current.y + 2; j++) {
                i = current.x - 2;
                if (Position.correct(i, j) && this.field.notEnemy(i, j)) {
                    this.fear[i][j] = 2;
                }

                i = current.x + 2;
                if (Position.correct(i, j) && this.field.notEnemy(i, j)) {
                    this.fear[i][j] = 2;
                }
            }
        }
    }

    /**
     * @param position : position to check
     * @return if Harry do not afraid to go to this position
     */
    protected boolean noFear(Position position) {
        return this.fear[position.x][position.y] != 1;
    }

    /**
     * @return if Harry just found some item: if he just found the cloak, the method edit inspectors' perception.
     */
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

            this.field.mrFilch.currentPerception = 0;
            this.field.mrsNorris.currentPerception = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (this.field.scheme[i][j].compareTo("n") == 0 || this.field.scheme[i][j].compareTo("f") == 0) {
                        if ((this.hp.norrisFound && this.field.scheme[i][j].compareTo("n") == 0) ||
                                (this.hp.filchFound && this.field.scheme[i][j].compareTo("f") == 0) ||
                                (this.hp.memory[i][j].compareTo("n") == 0) || (this.hp.memory[i][j].compareTo("f") == 0)) {
                            this.hp.memory[i][j] = "+";
                        }
                        this.field.scheme[i][j] = "·";
                    } else if (this.hp.memory[i][j].compareTo("b") == 0) {
                        this.hp.memory[i][j] = this.field.scheme[i][j];
                    }
                }
            }
            this.hp.memory[this.field.mrFilch.position.x][this.field.mrFilch.position.y] = "F";
            this.hp.memory[this.field.mrsNorris.position.x][this.field.mrsNorris.position.y] = "N";
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return if Harry cannot access the book right now
     */
    boolean cannotAccessBook() {
        if (this.hp.hasBook) {
            return false;
        } else {
            return this.noUnknownCells();
        }
    }

    /**
     * @return if Harry cannot access the exit right now
     */
    boolean cannotAccessExit() {
        return this.hp.memory[this.hp.field.exit.x][this.hp.field.exit.y].compareTo("b") == 0;
    }

    /**
     * @return if there are no unknown cells on the map
     */
    boolean noUnknownCells() {
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

    /**
     * The method finds bounded area with DFS.
     * @param current : current position
     */
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

    /**
     * @return if Harry have found bounded area on the field.
     */
    boolean boundedAreaExists() {
        this.generateBDFirstSearch();
        this.findBoundedArea(this.hp.position);
        boolean ans = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.BDFirstSearch[i][j] == 0 && this.hp.memory[i][j].compareTo("b") != 0) {
                    ans = true;
                    this.hp.memory[i][j] = "b";
                }
            }
        }
        return ans;
    }

    /**
     * @return closest to Harry unknown cell.
     */
    protected Position closestUnknown() {
        for (int radius = 1; radius < 9; radius++) {
            for (int i = this.hp.position.x - radius; i < this.hp.position.x + radius + 1; i++) {
                for (int j = this.hp.position.y - radius; j < this.hp.position.y + radius + 1; j++) {
                    if (Position.correct(i, j) && this.hp.memory[i][j].compareTo("·") == 0) {
                        return new Position(i, j);
                    }
                }
            }
        }
        return new Position(-1, -1);
    }

    /**
     * The method checks if Harry have found inspectors and if there is bounded area on the map
     * and prints Harry's memory.
     */
    protected void checkAndPrint() {
        this.step++;
        this.hp.updateMemory();

        if (!this.hp.norrisFound) {
            this.hp.checkNorris();
        }
        if (!this.hp.filchFound) {
            this.hp.checkFilch();
        }

        if (!this.harryCaught() && this.boundedAreaExists()) {
            System.out.println("BOUNDED AREA FOUND");
        }

        System.out.println("Memory:");
        this.hp.printMemory();
    }
}
