import java.util.LinkedList;

public class AStar extends Search {
    private int[][][] aStarCalculations;
    private int[][] roadMap;

    public AStar(HarryPotter hp, Field field) {
        super(hp, field);
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
        System.out.println("\nA_STAR SEARCH");
        this.field.newGame();
        this.getItem();
        this.path.add(this.hp.position.copy());

        while (!this.hp.endgame) {
            if (this.step == 12) {
                int breakpoint = 0;
            }

            this.restartCalculations();
            this.restartRoadMap();

            if (this.harryCaught()) {
                this.hp.endgame = true;
                System.out.println("YOU LOSE: HARRY WAS CAUGHT.");
            } else if (this.cannotAccessExit()) {
                this.hp.endgame = true;
                System.out.println("YOU CANNOT ACCESS THE EXIT. BAD LUCK:I");
            } else if (this.cannotAccessBook()) {
                this.hp.endgame = true;
                System.out.println("YOU CANNOT ACCESS THE BOOK. BAD LUCK:I");
            } else if (!this.hp.exitChecked) {
                if (this.hp.position.equals(field.exit)) {
                    this.hp.exitChecked = true;
                } else {
                    this.goTo(this.field.exit);
                }
            } else if (this.hp.hasBook) {
                if (this.hp.position.equals(field.exit)) {
                    this.hp.endgame = true;
                    this.result = "WON";
                    System.out.println("YOU WON");
                } else {
                    this.goTo(this.field.exit);
                }
            } else {
                Position target = this.closestUnknown();
                this.goTo(target);
            }
        }
    }

    private void goTo(Position target) {
        LinkedList<Position> shortestWay = new LinkedList<>();
        this.aStarCalculations[this.hp.position.x][this.hp.position.y][0] = 0;
        this.aStarCalculations[this.hp.position.x][this.hp.position.y][1] =
                Math.abs(target.x - this.hp.position.x) + Math.abs(target.y - this.hp.position.y);

        this.updateCalculations(this.hp.position, target);
        this.roadMap[this.hp.position.x][this.hp.position.y] = 1;

        do {
            Position position = this.doStep();
            this.roadMap[position.x][position.y] = 1;
            this.updateCalculations(position, target);
        } while (this.aStarCalculations[target.x][target.y][0] >= 10000);
        this.findShortestPathAndGo(field, target, shortestWay);
    }

    private void findShortestPathAndGo(Field field, Position target, LinkedList<Position> shortestWay) {
        this.restartRoadMap();
        shortestWay.add(target);
        this.roadMap[target.x][target.y] = 1;
        Position position = this.findPartOfShortestWay(target);
        while (!position.equals(this.hp.position)) {
//            this.printCalculations();
            shortestWay.add(position);
            this.roadMap[position.x][position.y] = 1;
            position = this.findPartOfShortestWay(position);
        }

        for (int i = shortestWay.size() - 1; i >= 0; i--) {
            position = shortestWay.get(i);
            if (this.hp.memory[position.x][position.y].compareTo("F") == 0 ||
                    this.hp.memory[position.x][position.y].compareTo("N") == 0 ||
                    this.hp.memory[position.x][position.y].compareTo("f") == 0 ||
                    this.hp.memory[position.x][position.y].compareTo("n") == 0) {
                break;
            } else {
                this.hp.memory[this.hp.position.x][this.hp.position.y] = "x";
                this.hp.position = position;
                this.path.add(position);

                System.out.println("STEP " + (this.step + 1));
                this.getItem();
                this.checkAndPrint(field);
            }
        }
    }

    private void updateCalculations(Position current, Position target) {
        int sum, heuristics, newSum, newHeuristics;
        for (int i = current.x - 1; i < current.x + 2; i++) {
            for (int j = current.y - 1; j < current.y + 2; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9 && this.hp.memory[i][j].compareTo("F") != 0 &&
                        this.hp.memory[i][j].compareTo("N") != 0 && this.hp.memory[i][j].compareTo("f") != 0 &&
                        this.hp.memory[i][j].compareTo("n") != 0) {
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
        int minGain = 10000;
        int x = -1, y = -1;

        for (int i = position.x - 1; i < position.x + 2; i++) {
            for (int j = position.y - 1; j < position.y + 2; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9 && this.roadMap[i][j] == 0) {
                    if (minGain > this.aStarCalculations[i][j][0]) {
                        minGain = this.aStarCalculations[i][j][0];
                        x = i;
                        y = j;
                    }
                }
            }
        }
        return new Position(x, y);
    }

    private Position doStep() {
        int minSum = 10000;
        int minHeuristics = 10000;
        int x = -1, y = -1;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.roadMap[i][j] == 0) {
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
