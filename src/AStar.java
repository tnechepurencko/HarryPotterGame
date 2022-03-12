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
            shortestWay.add(position);
            this.roadMap[position.x][position.y] = 1;
            position = this.findPartOfShortestWay(position);
        }

        for (int i = shortestWay.size() - 1; i >= 0; i--) {
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "x";
            this.hp.position = shortestWay.get(i);

            this.checkAndPrint(field, stepNumber);
            stepNumber++;
        }
    }

    private void updateCalculations(Position current, Position target) {
        int sum, heuristics;
        for (int i = current.x - 1; i < current.x + 2; i++) {
            for (int j = current.y - 1; j < current.y + 2; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9) {
                    if (this.hp.memory[i][j].compareTo("n") != 0 && this.hp.memory[i][j].compareTo("f") != 0 &&
                            this.hp.memory[i][j].compareTo("N") != 0 && this.hp.memory[i][j].compareTo("F") != 0) {
                        sum = this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1];
                        heuristics = this.aStarCalculations[i][j][1];
                         if (sum > Math.abs(target.x - current.x) + Math.abs(target.y - current.y) +
                                Math.abs(i - current.x) + Math.abs(j - current.y)) {
                            this.aStarCalculations[i][j][0] = Math.abs(i - current.x) +
                                    Math.abs(j - current.y);
                            this.aStarCalculations[i][j][1] = Math.abs(target.x - current.x) +
                                    Math.abs(target.y - current.y);
                        } else if (sum == Math.abs(target.x - current.x) + Math.abs(target.y - current.y) +
                                Math.abs(i - current.x) + Math.abs(j - current.y) &&
                                heuristics > Math.abs(i - current.x) + Math.abs(j - current.y)) {
                            this.aStarCalculations[i][j][0] = Math.abs(i - current.x) +
                                    Math.abs(j - current.y);
                            this.aStarCalculations[i][j][1] = Math.abs(target.x - current.x) +
                                    Math.abs(target.y - current.y);
                        }
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
        return this.findOptimalStep(position.x - 1, position.x + 2, position.y - 1, position.y + 2);
    }

    private Position doStep() {
        return this.findOptimalStep(0, 8, 0, 8);
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
}
