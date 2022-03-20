import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Backtracking extends Search {

    public Backtracking(HarryPotter hp, Field field) {
        super(hp, field);
    }

    /**
     * Backtracking search implementation.
     */
    public void search() {
        System.out.println("\nBACKTRACKING SEARCH");
        Instant startTime = Instant.now();
        this.field.newGame();
        this.hp.updateMemory();
        this.getItem();

        this.path.add(this.hp.position.copy());
        Stack<Position> stack = new Stack<>();
        stack.push(this.hp.position.copy());

        while (!this.hp.endgame) {
            if (this.harryCaught()) {
                this.hp.endgame = true;
                System.out.println("YOU LOSE: HARRY WAS CAUGHT.");
            } else if (this.cannotAccessExit() && this.noUnknownCells()) {
                this.hp.endgame = true;
                System.out.println("YOU CANNOT ACCESS THE EXIT. BAD LUCK:I");
            } else if (this.cannotAccessBook()) {
                this.hp.endgame = true;
                System.out.println("YOU CANNOT ACCESS THE BOOK. BAD LUCK:I");
            } else if (!this.hp.exitChecked && !this.cannotAccessExit()) {
                if (this.hp.position.equals(this.field.exit)) {
                    this.hp.exitChecked = true;
                } else {
                    this.goTo(this.field.exit, stack);
                }
            } else if (this.hp.hasBook && this.hp.position.equals(this.field.exit)) {
                this.hp.endgame = true;
                this.result = "WON";
                System.out.println("YOU WON");
            } else if (this.hp.hasBook && !this.cannotAccessExit()) {
                this.goTo(this.field.exit, stack);
            } else if (this.noUnknownAdjacentCells() && !stack.empty() && this.canGoBack()) {
                System.out.println("ROLLBACK");

                this.rollback(stack);
                this.hp.updateMemory();
            } else {
                Position position = this.closestUnknown();
                this.goTo(position, stack);
            }
        }
        this.runtime += Duration.between(startTime, Instant.now()).toNanos();
    }

    /**
     * The methods rollbacks Harry until he is next to unknown cell.
     * @param stack : current stack
     */
    private void rollback(Stack<Position> stack) {
        Position position;
        while (this.noUnknownAdjacentCells()) {
            position = stack.pop();
            this.hp.memory[position.x][position.y] = "+";
            this.hp.position = stack.peek().copy();
        }
    }

    /**
     * @return if there is no unknown adjacent to Harry cell.
     */
    private boolean noUnknownAdjacentCells() {
        for (int i = this.hp.position.x - 1; i < this.hp.position.x + 2; i++) {
            for (int j = this.hp.position.y - 1; j < this.hp.position.y + 2; j++) {
                if (Position.correct(i, j)) {
                    if (this.hp.memory[i][j].compareTo("·") == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * The method checks if Harry was next to some unknown cell.
     * @return if rollback is possible.
     */
    private boolean canGoBack() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.hp.memory[i][j].compareTo("x") == 0) {
                    for (int i1 = i - 1; i1 < i + 2; i1++) {
                        for (int j1 = j - 1; j1 < j + 2; j1++) {
                            if (Position.correct(i1, j1)) {
                                if (this.hp.memory[i1][j1].compareTo("·") == 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * The method finds the shortest path to the target and makes Harry go this way.
     * @param target : where Harry wants to go
     * @param stack : current stuck
     */
    private void goTo(Position target, Stack<Position> stack) {
        LinkedList<Position[]> shortestWay = new LinkedList<>();
        Queue<Position[]> queue = new LinkedList<>();

        this.generateBDFirstSearch();
        Position[] p = {this.hp.position, this.hp.position};
        queue.add(p);
        this.generateFear();
        boolean pathReliable = this.findShortestWay(target, shortestWay, queue);
        if (!pathReliable) {
            shortestWay = new LinkedList<>();
            queue = new LinkedList<>();

            this.generateBDFirstSearch();
            queue.add(p);
            this.goToDangerousArea(target, shortestWay, queue);
            Position newPos = shortestWay.get(shortestWay.size() - 2)[0].copy();
            this.doStep(newPos, stack);
            return;
        }

        for (int i = shortestWay.size() - 2; i > -1; i--) {
            Position newPos = shortestWay.get(i)[0].copy();
            if (this.field.notEnemy(newPos) && ((!this.cannotAccessExit() && !this.cannotAccessBook()) || !this.hp.hasCloak)) {
                this.doStep(newPos, stack);
            } else {
                break;
            }
        }
    }

    /**
     * The method makes Harry go to the "newPos".
     * @param newPos : position to go
     * @param stack: current stack
     */
    private void doStep(Position newPos, Stack<Position> stack) {
        this.hp.memory[this.hp.position.x][this.hp.position.y] = "x";
        this.hp.position = newPos;
        this.path.add(newPos);

        System.out.println("STEP " + (this.step + 1));
        boolean gotItem = this.getItem();

        if (gotItem) {
            this.markUsefulRoute(stack);
        }
        stack.push(this.hp.position.copy());
        this.checkAndPrint();
    }

    /**
     * The method finds the shortest way to the target.
     * @param target : where Harry wants to go
     * @param shortestWay : list with the shortest way to the target
     * @param queue : queue we use for BFS
     * @return if shortest way is found
     */
    private boolean findShortestWay(Position target, LinkedList<Position[]> shortestWay, Queue<Position[]> queue) {
        if (!queue.isEmpty()) {
            Position[] positions = queue.poll();
            this.updateFear(positions[0]);
            if (positions[0].equals(target)) {
                Position[] p = {positions[0].copy(), positions[1].copy()};
                shortestWay.add(p);
                return true;
            } else {
                this.BDFirstSearch[positions[0].x][positions[0].y] = 1;
                for (Position delta: DELTAS) {
                    Position newPos = positions[0].sum(delta);
                    if (newPos.correct() && this.noFear(newPos) &&
                            this.BDFirstSearch[newPos.x][newPos.y] == 0 && this.hp.notEnemy(newPos)) {
                        Position[] p = {newPos.copy(), positions[0].copy()};
                        queue.add(p);
                        this.BDFirstSearch[newPos.x][newPos.y] = 1;
                    }
                }
                if (this.findShortestWay(target, shortestWay, queue)) {
                    if (shortestWay.get(shortestWay.size() - 1)[1].equals(positions[0])) {
                        Position[] p = {positions[0].copy(), positions[1].copy()};
                        shortestWay.add(p);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The method is used when Harry have to go to the dangerous area (fear[i][j] == 1).
     * @param target : where Harry wants to go
     * @param shortestWay : list with the shortest way to the target
     * @param queue : queue we use for BFS
     * @return if shortest way is found
     */
    private boolean goToDangerousArea(Position target, LinkedList<Position[]> shortestWay, Queue<Position[]> queue) {
        if (!queue.isEmpty()) {
            Position[] positions = queue.poll();
            if (positions[0].equals(target)) {
                Position[] p = {positions[0].copy(), positions[1].copy()};
                shortestWay.add(p);
                return true;
            } else {
                this.BDFirstSearch[positions[0].x][positions[0].y] = 1;
                for (Position delta: DELTAS) {
                    Position newPos = positions[0].sum(delta);
                    if (newPos.correct() && this.BDFirstSearch[newPos.x][newPos.y] == 0 && this.hp.notEnemy(newPos)) {
                        Position[] p = {newPos.copy(), positions[0].copy()};
                        queue.add(p);
                        this.BDFirstSearch[newPos.x][newPos.y] = 1;
                    }
                }
                if (this.goToDangerousArea(target, shortestWay, queue)) {
                    if (shortestWay.get(shortestWay.size() - 1)[1].equals(positions[0])) {
                        Position[] p = {positions[0].copy(), positions[1].copy()};
                        shortestWay.add(p);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The method marks route which we cannot roll back (Harry just found an item -> the route became useful)
     * @param stack : current stack
     */
    private void markUsefulRoute(Stack<Position> stack) {
        while (!stack.empty()) {
            Position position = stack.peek();
            this.hp.memory[position.x][position.y] = "u";
            stack.pop();
        }
        stack.push(this.hp.position.copy());
    }
}
