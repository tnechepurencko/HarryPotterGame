public class HarryPotter extends Person {
    int scenario;
    String[][] memory;
    Field field;

    boolean norrisFound;
    boolean filchFound;
    boolean hasBook;
    boolean hasCloak;
    boolean endgame;
    boolean exitChecked; // to see if the cloak is on exit cell

    public HarryPotter(int scenario, Position position, Field field) {
        super(position, "H");
        this.field = field;
        this.scenario = scenario;
        this.memory = new String[9][9];

        this.norrisFound = false;
        this.filchFound = false;
        this.hasBook = false;
        this.hasCloak = false;
        this.endgame = false;
        this.exitChecked = false;
        this.generateMemory();
    }

    void updateHarry() {
        this.position = new Position(0, 0);
        this.norrisFound = false;
        this.filchFound = false;
        this.hasBook = false;
        this.hasCloak = false;
        this.endgame = false;
        this.exitChecked = false;
        this.generateMemory();
        this.prepareHarry();
    }

    /**
     * The method fills memory it with "·" symbol.
     */
    void generateMemory() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.memory[i][j] = "·";
            }
        }
    }

    /**
     * The method adds location of exit to memory.
     */
    void prepareHarry() {
        this.memory[this.field.exit.x][this.field.exit.y] = "E";
        this.updateMemory();
    }

    /**
     * The method checks if Harry can determine location of Norris.
     */
    void checkNorris() {
        int x = this.field.mrsNorris.position.x, y = this.field.mrsNorris.position.y;
        int count = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.memory[i][j].compareTo("N") == 0) {
                    System.out.println("NORRIS FOUND");
                    this.norrisFound = true;
                    return;
                } else if (this.memory[i][j].compareTo("n") == 0) {
                    count++;
                }
                if (count > 3) {
                    this.norrisFound = true;
                    System.out.println("NORRIS FOUND");
                    for (int i1 = x - 1; i1 < x + 2; i1++) {
                        for (int j1 = y - 1; j1 < y + 2; j1++) {
                            if (Position.correct(i1, j1)) {
                                this.memory[i1][j1] = "n";
                            }
                        }
                    }
                    this.memory[x][y] = "N";
                    return;
                }
            }
        }
    }

    /**
     * The method checks if Harry can determine location of Filch.
     */
    void checkFilch() {
        int x = this.field.mrFilch.position.x, y = this.field.mrFilch.position.y;
        int count = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.memory[i][j].compareTo("F") == 0) {
                    System.out.println("FILCH FOUND");
                    this.filchFound = true;
                    return;
                } else if (this.memory[i][j].compareTo("f") == 0) {
                    count++;
                }
                if (count > 5) {
                    this.filchFound = true;
                    System.out.println("FILCH FOUND");
                    for (int i1 = x - 2; i1 < x + 3; i1++) {
                        for (int j1 = y - 2; j1 < y + 3; j1++) {
                            if (Position.correct(i1, j1)) {
                                this.memory[i1][j1] = "f";
                            }
                        }
                    }
                    this.memory[x][y] = "F";
                    return;
                }
            }
        }
    }

    /**
     * The method adds cells Harry discovered to his memory.
     */
    void updateMemory() {
        this.field.update();

        if (this.scenario == 1) {
            for (int i = this.position.x - 1; i < this.position.x + 2; i++) {
                for (int j = this.position.y - 1; j < this.position.y + 2; j++) {
                    if (Position.correct(i, j)) {
                        this.memorizeEnemy(i, j);
                    }
                }
            }
        } else if (this.scenario == 2) {
            this.memory[this.position.x][this.position.y] = this.field.scheme[this.position.x][this.position.y];

            int i, j;
            for (i = this.position.x - 1; i < this.position.x + 2; i++) {
                j = this.position.y - 2;
                if (i > -1 && i < 9 && j > -1) {
                    this.memorizeEnemy(i, j);
                }

                j = this.position.y + 3;
                if (i > -1 && i < 9 && j < 9) {
                    this.memorizeEnemy(i, j);
                }
            }
        }

        this.memory[this.position.x][this.position.y] = "H";
    }

    boolean notEnemy(Position position) {
        return this.memory[position.x][position.y].compareTo("F") != 0 &&
                this.memory[position.x][position.y].compareTo("N") != 0 &&
                this.memory[position.x][position.y].compareTo("f") != 0 &&
                this.memory[position.x][position.y].compareTo("n") != 0;
    }

    boolean notEnemy(int x, int y) {
        return this.memory[x][y].compareTo("F") != 0 && this.memory[x][y].compareTo("N") != 0 &&
                this.memory[x][y].compareTo("f") != 0 && this.memory[x][y].compareTo("n") != 0;
    }

    private void memorizeEnemy(int i, int j) {
        if (!this.field.notEnemy(i, j)) {
            this.memory[i][j] = this.field.scheme[i][j];
        }
    }

    /**
     * The method prints Harry's memory.
     */
    void printMemory() {
        for (int i = 8; i > -1; i--) {
            for (int j = 0; j < 9; j++) {
                System.out.print(this.memory[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }
}


