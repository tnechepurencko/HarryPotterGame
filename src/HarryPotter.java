public class HarryPotter extends Person {
    int scenario;
    String[][] memory;

    boolean norrisFound;
    boolean filchFound;
    boolean hasBook;
    boolean hasCloak;
    boolean endgame;

    public HarryPotter(int scenario, Position position) {
        super(position, "H");
        this.norrisFound = false;
        this.filchFound = false;
        this.hasBook = false;
        this.hasCloak = false;
        this.endgame = false;

        this.scenario = scenario;
        this.generateMemory();
    }

    /**
     * The method initializes "memory" and fills it with "·" symbol.
     */
    void generateMemory() {
        this.memory = new String[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.memory[i][j] = "·";
            }
        }
    }

    /**
     * The method adds location of exit to memory.
     * @param exit: position of exit
     * @param field: current field
     */
    void prepareHarry(Position exit, Field field) {
        this.memory[exit.x][exit.y] = "E";
        updateMemory(field);
    }

    /**
     * The method checks if Harry can determine location of Norris.
     * @param field: current field
     */
    void checkNorris(Field field) {
        int x = field.mrsNorris.position.x, y = field.mrsNorris.position.y;
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
                            if (i1 > -1 && i1 < 9 && j1 > -1 && j1 < 9) {
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
     * @param field: current field
     */
    void checkFilch(Field field) {
        int x = field.mrFilch.position.x, y = field.mrFilch.position.y;
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
                            if (i1 > -1 && i1 < 9 && j1 > -1 && j1 < 9) {
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
     * @param field: current field
     */
    void updateMemory(Field field) {
        field.update();

//        if (!this.filchFound || !this.norrisFound) {
            if (this.scenario == 1) {
                for (int i = this.position.x - 1; i < this.position.x + 2; i++) {
                    for (int j = this.position.y - 1; j < this.position.y + 2; j++) {
                        if (i > -1 && i < 9 && j > -1 && j < 9) {
                            this.memorizeEnemy(field, i, j);
                        }
                    }
                }
            } else if (this.scenario == 2) {
                this.memory[this.position.x][this.position.y] = field.scheme[this.position.x][this.position.y];

                int i, j;
                for (i = this.position.x - 1; i < this.position.x + 2; i++) {
                    j = this.position.y - 2;
                    if (i > -1 && i < 9 && j > -1) {
                        this.memorizeEnemy(field, i, j);
                    }

                    j = this.position.y + 3;
                    if (i > -1 && i < 9 && j < 9) {
                        this.memorizeEnemy(field, i, j);
                    }
                }
            }
//        }

        this.memory[this.position.x][this.position.y] = "H";
    }

    private void memorizeEnemy(Field field, int i, int j) {
        if (field.scheme[i][j].compareTo("F") == 0 || field.scheme[i][j].compareTo("f") == 0 ||
                field.scheme[i][j].compareTo("N") == 0 || field.scheme[i][j].compareTo("n") == 0) {
            this.memory[i][j] = field.scheme[i][j];
        }
    }

    /**
     * @return if Harry can move up
     */
    boolean canMoveUp() {
        return this.memory[this.position.x + 1][this.position.y].compareTo("f") != 0 &&
                this.memory[this.position.x + 1][this.position.y].compareTo("n") != 0;
    }

    /**
     * @return if Harry can move up-right
     */
    boolean canMoveUR() {
        return canMoveUp() && canMoveRight();
    }

    /**
     * @return if Harry can move right
     */
    boolean canMoveRight() {
        return this.memory[this.position.x][this.position.y + 1].compareTo("f") != 0 &&
                this.memory[this.position.x][this.position.y + 1].compareTo("n") != 0;
    }

    /**
     * @return if Harry can move right-down
     */
    boolean canMoveRD() {
        return canMoveRight() && canMoveDown();
    }

    /**
     * @return if Harry can move down
     */
    boolean canMoveDown() {
        return this.memory[this.position.x - 1][this.position.y].compareTo("f") != 0 &&
                this.memory[this.position.x - 1][this.position.y].compareTo("n") != 0;
    }

    /**
     * @return if Harry can move down-left
     */
    boolean canMoveDL() {
        return canMoveDown() && canMoveLeft();
    }

    /**
     * @return if Harry can move left
     */
    boolean canMoveLeft() {
        return this.memory[this.position.x][this.position.y - 1].compareTo("f") != 0 &&
                this.memory[this.position.x][this.position.y - 1].compareTo("n") != 0;
    }

    /**
     * @return if Harry can move up-left
     */
    boolean canMoveUL() {
        return canMoveLeft() && canMoveUp();
    }

    /**
     * The method makes Harry move up.
     */
    void moveUp() {
        this.position.x++;
    }

    /**
     * The method makes Harry move up-right.
     */
    void moveUR() {
        this.position.x++;
        this.position.y++;
    }

    /**
     * The method makes Harry move right.
     */
    void moveRight() {
        this.position.y++;
    }

    /**
     * The method makes Harry move right-down.
     */
    void moveRD() {
        this.position.x--;
        this.position.y++;
    }

    /**
     * The method makes Harry move down.
     */
    void moveDown() {
        this.position.x--;
    }

    /**
     * The method makes Harry move down-left.
     */
    void moveDL() {
        this.position.x--;
        this.position.y--;
    }

    /**
     * The method makes Harry move left.
     */
    void moveLeft() {
        this.position.y--;
    }

    /**
     * The method makes Harry move up-left.
     */
    void moveUL() {
        this.position.x++;
        this.position.y--;
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


