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

    /**
     * The method resets Harry's parameters.
     */
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
        int count = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.memory[i][j].compareTo("N") == 0) {
                    this.norrisFound = true;
                    System.out.println("NORRIS FOUND");
                    inspectorFound(this.field.mrsNorris);
                    return;
                } else if (this.memory[i][j].compareTo("n") == 0) {
                    count++;
                }
                if (count > 3) {
                    this.norrisFound = true;
                    System.out.println("NORRIS FOUND");
                    inspectorFound(this.field.mrsNorris);
                    return;
                }
            }
        }
    }

    /**
     * The method checks if Harry can determine location of Filch.
     */
    void checkFilch() {
        int count = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.memory[i][j].compareTo("F") == 0) {
                    this.filchFound = true;
                    System.out.println("FILCH FOUND");
                    this.inspectorFound(this.field.mrFilch);
                    return;
                } else if (this.memory[i][j].compareTo("f") == 0) {
                    count++;
                }
                if ((this.scenario == 1 && count > 5) || (this.scenario == 2 && count > 18)) {
                    this.filchFound = true;
                    System.out.println("FILCH FOUND");
                    inspectorFound(this.field.mrFilch);
                    return;
                }
            }
        }
    }

    /**
     * The method makes Harry to see the whole perception zone of the inspector.
     * @param i : inspector that was found
     */
    private void inspectorFound(Inspector i) {
        for (int i1 = i.position.x - i.staticPerception; i1 < i.position.x + 1 + i.staticPerception; i1++) {
            for (int j1 = i.position.y - i.staticPerception; j1 < i.position.y + 1 + i.staticPerception; j1++) {
                if (Position.correct(i1, j1) && !this.position.equals(i1, j1)) {
                    if (this.hasCloak) {
                        if ((this.position.x != i1 || this.position.y != j1) &&
                                (!this.field.mrFilch.position.equals(i1, j1) &&
                                        !this.field.mrsNorris.position.equals(i1, j1))) {
                            this.memory[i1][j1] = "+";
                        }
                    } else {
                        this.memory[i1][j1] = i.symbol.toLowerCase();
                    }
                }
            }
        }
        this.memory[i.position.x][i.position.y] = i.symbol;
    }

    /**
     * Logic of the method:
     * if Harry knows an upper-left cell & lower-right cell of enemy's perception, then he knows that the rectangle
     * between this cells is filled with enemies' perception.
     */
    void findInspectorCells() {
        int xMinF = 10000, xMaxF = -10000, yMinF = 10000, yMaxF = -10000;
        int xMinN = 10000, xMaxN = -10000, yMinN = 10000, yMaxN = -10000;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!this.norrisFound && this.memory[i][j].compareTo("n") == 0) {
                    if (i < xMinN) {
                        xMinN = i;
                    }
                    if (j < yMinN) {
                        yMinN = j;
                    }
                    if (i > xMaxN) {
                        xMaxN = i;
                    }
                    if (j > yMaxN) {
                        yMaxN = j;
                    }
                }

                if (!this.filchFound && this.memory[i][j].compareTo("f") == 0) {
                    if (i < xMinF) {
                        xMinF = i;
                    }
                    if (j < yMinF) {
                        yMinF = j;
                    }
                    if (i > xMaxF) {
                        xMaxF = i;
                    }
                    if (j > yMaxF) {
                        yMaxF = j;
                    }
                }
            }
        }

        if (!this.norrisFound) {
            for (int i = xMinN; i < xMaxN + 1; i++) {
                for (int j = yMinN; j < yMaxN + 1; j++) {
                    this.memory[i][j] = "n";
                }
            }
        }

        if (!this.filchFound) {
            for (int i = xMinF; i < xMaxF + 1; i++) {
                for (int j = yMinF; j < yMaxF + 1; j++) {
                    this.memory[i][j] = "f";
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
            this.memory[this.position.x][this.position.y] = this.field.scheme[this.position.x][this.position.y]; // TODO why

            int i, j;
            for (i = this.position.x - 1; i < this.position.x + 2; i++) {
                j = this.position.y - 2;
                if (Position.correct(i, j)) {
                    this.memorizeEnemy(i, j);
                }

                j = this.position.y + 2;
                if (Position.correct(i, j)) {
                    this.memorizeEnemy(i, j);
                }
            }

            for (j = this.position.y - 1; j < this.position.y + 2; j++) {
                i = this.position.x - 2;
                if (Position.correct(i, j)) {
                    this.memorizeEnemy(i, j);
                }

                i = this.position.x + 2;
                if (Position.correct(i, j)) {
                    this.memorizeEnemy(i, j);
                }
            }
        }
        this.findInspectorCells();
        this.memory[this.position.x][this.position.y] = "H";
    }

    /**
     * @param position : position to check
     * @return if an enemy is on this position
     */
    boolean notEnemy(Position position) {
        return this.memory[position.x][position.y].compareTo("F") != 0 &&
                this.memory[position.x][position.y].compareTo("N") != 0 &&
                this.memory[position.x][position.y].compareTo("f") != 0 &&
                this.memory[position.x][position.y].compareTo("n") != 0;
    }

    /**
     * @param x : position.x
     * @param y : position.y
     * @return if an enemy is on this position
     */
    boolean notEnemy(int x, int y) {
        return this.memory[x][y].compareTo("F") != 0 && this.memory[x][y].compareTo("N") != 0 &&
                this.memory[x][y].compareTo("f") != 0 && this.memory[x][y].compareTo("n") != 0;
    }

    /**
     * The method checks if an enemy is on this position. If so, makes Harry see it.
     * @param i : position.x
     * @param j : position.y
     */
    private void memorizeEnemy(int i, int j) {
        if (!this.field.notEnemy(i, j) && this.memory[i][j].compareTo("b") != 0) {
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


