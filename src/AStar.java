import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

public class AStar extends Search {
    private int[][][] aStarCalculations; // [i][j][0]-gain, [i][j][1]-heuristics
    private int[][] roadMap; // 1-Harry updated calculations on this cell, 0-opposite

    public AStar(HarryPotter hp, Field field) {
        super(hp, field);
        this.generateLists();
    }

    /**
     * The method initializes calculations list & roadMap & fills them with their initial values.
     */
    private void generateLists() {
        this.aStarCalculations = new int[9][9][2];
        this.roadMap = new int[9][9];
        this.restartCalculations();
        this.restartRoadMap();
    }

    /**
     * The method fills "roadMap" with zeros.
     */
    private void restartRoadMap() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.roadMap[i][j] = 0;
            }
        }
    }

    /**
     * The method fills calculations list with 10000s.
     */
    private void restartCalculations() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 2; k++) {
                    aStarCalculations[i][j][k] = 10000;
                }
            }
        }
    }

    /**
     * A* search implementation.
     */
    public void search() {
        System.out.println("\nA_STAR SEARCH");
        Instant startTime = Instant.now();
        this.field.newGame();
        this.hp.updateMemory();
        this.getItem();

        this.path.add(this.hp.position.copy());
        while (!this.hp.endgame) {
            this.restartCalculations();
            this.restartRoadMap();

            if (this.harryCaught()) {
                this.hp.endgame = true;
                System.out.println("YOU LOSE: HARRY WAS CAUGHT.");
            } else if (this.cannotAccessExit() && noUnknownCells()) {
                this.hp.endgame = true;
                System.out.println("YOU CANNOT ACCESS THE EXIT. BAD LUCK:I");
            } else if (this.cannotAccessBook()) {
                this.hp.endgame = true;
                System.out.println("YOU CANNOT ACCESS THE BOOK. BAD LUCK:I");
            } else if (!this.hp.exitChecked && !this.cannotAccessExit()) {
                if (this.hp.position.equals(this.field.exit)) {
                    this.hp.exitChecked = true;
                } else {
                    this.generateFear();
                    this.goTo(this.field.exit);
                }
            } else if (this.hp.hasBook && this.hp.position.equals(this.field.exit)) {
                this.hp.endgame = true;
                this.result = "WON";
                System.out.println("YOU WON");
            } else if (this.hp.hasBook && !this.cannotAccessExit()) {
                this.generateFear();
                this.goTo(this.field.exit);
            } else {
                Position target = this.closestUnknown();
                this.generateFear();
                this.goTo(target);
            }
        }
        this.runtime += Duration.between(startTime, Instant.now()).toNanos();
    }

    /**
     * The method do calculations until it finds some way to the target, finds the shortest path,
     * and makes Harry go this way.
     * @param target : where Harry wants to go
     */
    private void goTo(Position target) {
        LinkedList<Position> shortestWay = new LinkedList<>();
        this.aStarCalculations[this.hp.position.x][this.hp.position.y][0] = 0;
        this.aStarCalculations[this.hp.position.x][this.hp.position.y][1] =
                Math.abs(target.x - this.hp.position.x) + Math.abs(target.y - this.hp.position.y);

        this.updateFear(this.hp.position);
        this.updateCalculations(this.hp.position, target);
        this.roadMap[this.hp.position.x][this.hp.position.y] = 1;

        do {
            Position position = this.cellToResearch();
            this.roadMap[position.x][position.y] = 1;
            this.updateFear(position);
            this.updateCalculations(position, target);
        } while (this.aStarCalculations[target.x][target.y][0] >= 10000);

        this.findShortestPathAndGo(target, shortestWay);
    }

    /**
     * The method is used when Harry have to go to the dangerous area (fear[i][j] == 1).
     * @param shortestWay : list with the shortest way to the target
     */
    private void goToDangerousArea(LinkedList<Position> shortestWay) {
        Position newPos = shortestWay.get(shortestWay.size() - 1);
        this.doStep(newPos);
    }

    /**
     * The method finds the shortest path to the target & makes Harry go to it.
     * @param target : where Harry wants to go
     * @param shortestWay : list with the shortest way to the target
     */
    private void findShortestPathAndGo(Position target, LinkedList<Position> shortestWay) {
        this.restartRoadMap();
        shortestWay.add(target);
        this.roadMap[target.x][target.y] = 1;
        Position newPos = this.findPartOfShortestWay(target);
        while (!newPos.equals(this.hp.position)) {
            shortestWay.add(newPos);
            this.roadMap[newPos.x][newPos.y] = 1;
            newPos = this.findPartOfShortestWay(newPos);
        }

        if (this.aStarCalculations[shortestWay.get(shortestWay.size() - 1).x][shortestWay.get(shortestWay.size() - 1).y][0] > 1000) {
            this.goToDangerousArea(shortestWay);
            return;
        }

        for (int i = shortestWay.size() - 1; i >= 0; i--) {
            newPos = shortestWay.get(i);
            if (!this.hp.notEnemy(newPos)) {
                break;
            } else {
                this.doStep(newPos);
            }
        }
    }

    /**
     * The method makes Harry go to the "newPos".
     * @param newPos : position to go
     */
    private void doStep(Position newPos) {
        this.hp.memory[this.hp.position.x][this.hp.position.y] = "x";
        this.hp.position = newPos;
        this.path.add(newPos);
        this.getItem();

        System.out.println("STEP " + (this.step + 1));
        this.checkAndPrint();
    }

    /**
     * @param current : current position
     * @return adjacent position with minimal gain & heuristics
     */
    private Position findPartOfShortestWay(Position current) {
        int minGain = 10000;
        int minHeuristics = 10000;
        int x = -1, y = -1;

        for (int i = current.x - 1; i < current.x + 2; i++) {
            for (int j = current.y - 1; j < current.y + 2; j++) {
                if (Position.correct(i, j) && this.roadMap[i][j] == 0) {
                    if (minGain > this.aStarCalculations[i][j][0]) {
                        minGain = this.aStarCalculations[i][j][0];
                        minHeuristics = this.aStarCalculations[i][j][1];
                        x = i;
                        y = j;
                    } else if (minGain == this.aStarCalculations[i][j][0] && minHeuristics > this.aStarCalculations[i][j][1]) {
                        minHeuristics = this.aStarCalculations[i][j][1];
                        x = i;
                        y = j;
                    }
                }
            }
        }
        return new Position(x, y);
    }

    /**
     * The method looks through the area around the current position & updates calculations in this area (if new
     * calculation has smaller sum and heuristics, it replaces the old one.
     * Cost: go to a safe cell (fear = 2) - 1 point, go to a dangerous cell (fear = 1) - 1000 points)
     * @param current : current position
     * @param target : where Harry wants to go
     */
    private void updateCalculations(Position current, Position target) {
        int sum, heuristics, newSum, newHeuristics;
        for (int i = current.x - 1; i < current.x + 2; i++) {
            for (int j = current.y - 1; j < current.y + 2; j++) {
                if (Position.correct(i, j) && this.noFear(new Position(i, j))) {
                    if (this.hp.notEnemy(i, j)) {
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
                } else if (Position.correct(i, j) && this.hp.notEnemy(i, j)) {
                    heuristics = this.aStarCalculations[i][j][1];
                    sum = this.aStarCalculations[i][j][0] + this.aStarCalculations[i][j][1] + 1000;
                    newHeuristics = Math.abs(i - target.x) + Math.abs(j - target.y);
                    newSum = newHeuristics + Math.max(Math.abs(i - current.x), Math.abs(j - current.y)) +
                            this.aStarCalculations[current.x][current.y][0] + 1000;

                    if (sum > newSum) {
                        this.aStarCalculations[i][j][0] = Math.max(Math.abs(i - current.x), Math.abs(j - current.y)) +
                                this.aStarCalculations[current.x][current.y][0] + 1000;
                        this.aStarCalculations[i][j][1] = Math.abs(i - target.x) + Math.abs(j - target.y);
                    } else if (sum == newSum && heuristics > newHeuristics) {
                        this.aStarCalculations[i][j][1] = Math.abs(i - target.x) + Math.abs(j - target.y);
                    }
                }
            }
        }
    }

    /**
     * @return cell with minimal sum & heuristics
     */
    private Position cellToResearch() {
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
}
