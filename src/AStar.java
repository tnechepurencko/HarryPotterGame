import java.util.LinkedList;

public class AStar extends Search {
    private int[][][] aStarCalculations;
    private int[][] roadMap;

    public AStar(HarryPotter hp) {
        super(hp);
        this.generateLists();
    }

    private void generateLists() {
        this.aStarCalculations = new int[9][9][2];
        this.roadMap = new int[9][9];
        this.restartCalculations();
        this.restartRoadMap();
    }

    private void restartRoadMap() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.roadMap[i][j] = 0;
            }
        }
    }

    public void search(Field field) {
        int step = 0;
        LinkedList<Position> shortestWay;

        while (!this.hp.exitLibrary) {
            if (step == 21) {
                int breakpoint = 0;
            }

            step++;
            this.restartCalculations();
            this.restartRoadMap();

            if (this.hp.hasBook) {
                if (this.hp.position.equals(field.exit)) {
                    this.hp.exitLibrary = true;
                    System.out.println("YOU WON");
                } else {
                    shortestWay = new LinkedList<>();
                    boolean exitFound = false;
                    this.updateCalculations(this.hp.position, field.exit);
                    this.roadMap[this.hp.position.x][this.hp.position.y] = 1;

                    while (!exitFound) {
                        Position position = this.doStep();
                        this.roadMap[position.x][position.y] = 1;
                        this.updateCalculations(position, field.exit);

                        if (this.aStarCalculations[field.exit.x][field.exit.y][0] < 10000) {
                            exitFound = true;
                        }
                    }

                    this.findShortestPathAndGo(field, field.exit, shortestWay, step);
                }
            } else {
                Position target = this.closestUnknown();

                this.aStarCalculations[this.hp.position.x][this.hp.position.y][0] = 0;
                this.aStarCalculations[this.hp.position.x][this.hp.position.y][1] =
                        Math.abs(target.x - this.hp.position.x) + Math.abs(target.y - this.hp.position.y);

                shortestWay = new LinkedList<>();
                this.updateCalculations(this.hp.position, target);
                this.roadMap[this.hp.position.x][this.hp.position.y] = 1;

                while (true) {
                    Position position = this.doStep();
                    this.roadMap[position.x][position.y] = 1;

                    if (this.aStarCalculations[target.x][target.y][0] < 10000) {
                        break;
                    }
                    this.updateCalculations(position, target);
                }

                this.findShortestPathAndGo(field, target, shortestWay, step);
            }
        }
    }

    private void findShortestPathAndGo(Field field, Position target, LinkedList<Position> shortestWay, int stepNumber) {
        this.restartRoadMap();
        shortestWay.add(target);
        this.roadMap[target.x][target.y] = 1;
        Position position = this.findPartOfShortestWay(target);
        while (!position.equals(this.hp.position)) {
            this.printCalculations();

            shortestWay.add(position);
            this.roadMap[position.x][position.y] = 1;
            position = this.findPartOfShortestWay(position);
        }

        for (int i = shortestWay.size() - 1; i >= 0; i--) {
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "x";
            this.hp.position = shortestWay.get(i);
            this.getItem(field);

            this.checkAndPrint(field, stepNumber);
            stepNumber++;
        }
    }

    private void updateCalculations(Position current, Position target) {
        int sum, heuristics, newSum, newHeuristics;
        for (int i = current.x - 1; i < current.x + 2; i++) {
            for (int j = current.y - 1; j < current.y + 2; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9 &&
                        this.hp.memory[i][j].compareTo("n") != 0 && this.hp.memory[i][j].compareTo("f") != 0 &&
                        this.hp.memory[i][j].compareTo("N") != 0 && this.hp.memory[i][j].compareTo("F") != 0) {
                    heuristics = this.aStarCalculations[i][j][1];
                    sum = this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1];
                    newHeuristics = Math.abs(i - target.x) + Math.abs(j - target.y);
                    newSum = newHeuristics + Math.max(Math.abs(i - current.x), Math.abs(j - current.y)) +
                            this.aStarCalculations[current.x][current.y][0];

                    if (sum > newSum) {
                        this.aStarCalculations[i][j][0] = Math.max(Math.abs(i - current.x), Math.abs(j - current.y)) +
                                this.aStarCalculations[current.x][current.y][0];
                        this.aStarCalculations[i][j][1] = Math.abs(i - target.x) + Math.abs(j - target.y);
                    } else if (sum == newSum && heuristics > newHeuristics) {
                        this.aStarCalculations[i][j][1] = Math.abs(i - target.x) + Math.abs(j - target.y);
                    }
                }
            }
        }
    }

    private void getItem(Field field) {
        if (this.hp.position.equals(field.book.position)) {
            field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasBook = true;
            System.out.println("BOOK FOUND");
        } else if (this.hp.position.equals(field.cloak.position)) {
            field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasCloak = true;
            System.out.println("CLOAK FOUND");
        }
    }

    private void restartCalculations() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 2; k++) {
                    aStarCalculations[i][j][k] = 10000;
                }
            }
        }
    }

    private Position findPartOfShortestWay(Position position) {
        return this.findOptimalWay(position.x - 1, position.x + 2, position.y - 1, position.y + 2);
    }

    private Position doStep() {
        return this.findOptimalStep(0, 9, 0, 9);
    }

    private Position findOptimalWay(int i0, int iMax, int j0, int jMax) {
        int minSum = 10000;
        int minGain = 10000;
        int x = -1, y = -1;

        for (int i = i0; i < iMax; i++) {
            for (int j = j0; j < jMax; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9 && this.roadMap[i][j] == 0) {
                    if (this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1] < minSum) {
                        minSum = this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1];
                        minGain = this.aStarCalculations[i][j][0];
                        x = i;
                        y = j;
                    } else if (this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1] == minSum &&
                            minGain > this.aStarCalculations[i][j][0]) {
                        minGain = this.aStarCalculations[i][j][0];
                        x = i;
                        y = j;
                    }
                }
            }
        }
        return new Position(x, y);
    }

    private Position findOptimalStep(int i0, int iMax, int j0, int jMax) {
        int minSum = 10000;
        int minHeuristics = 10000;
        int x = -1, y = -1;

        for (int i = i0; i < iMax; i++) {
            for (int j = j0; j < jMax; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9 && this.roadMap[i][j] == 0) {
                    if (this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1] < minSum) {
                        minSum = this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1];
                        minHeuristics = this.aStarCalculations[i][j][1];
                        x = i;
                        y = j;
                    } else if (this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1] == minSum &&
                            minHeuristics > this.aStarCalculations[i][j][1]) {
                        minHeuristics = this.aStarCalculations[i][j][1];
                        x = i;
                        y = j;
                    }
                }
            }
        }
        return new Position(x, y);
    }

    void printCalculations() {
        for (int i = 8; i > -1; i--) {
            for (int j = 0; j < 9; j++) {
                System.out.print(this.aStarCalculations[i][j][0] + " " + this.aStarCalculations[i][j][1]);
                if (this.aStarCalculations[i][j][0] != 10000) {
                    System.out.print("        ");
                }
                System.out.print("; ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
