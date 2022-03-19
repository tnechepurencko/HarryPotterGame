import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Backtracking extends Search {

    public Backtracking(HarryPotter hp, Field field) {
        super(hp, field);
    }

    public void search(Field field) {
        System.out.println("\nBACKTRACKING SEARCH");
        long startTime = System.currentTimeMillis();
        this.field.newGame();
        this.hp.updateMemory();
        this.getItem();

        this.path.add(this.hp.position.copy());
//        this.hp.updateMemory();
        Stack<Position> stack = new Stack<>();
        stack.push(this.hp.position.copy());

        while (!this.hp.endgame) {
            if (this.step == 1) {
                int bp = 123456;
            }

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
                    this.goTo(field.exit, stack);
                }
            } else if (this.hp.hasBook) {
                if (this.hp.position.equals(field.exit)) {
                    this.hp.endgame = true;
                    this.result = "WON";
                    System.out.println("YOU WON");
                } else {
                    this.goTo(field.exit, stack);
                }
            } else if (this.noUnknownAdjacentCells() && !stack.empty() && this.canGoBack()) {
                System.out.println("BACKTRACK");
                this.backtrack(stack);
                this.hp.updateMemory();
            } else {
                Position position = this.closestUnknown();
                this.goTo(position, stack);
            }
        }
        this.runtime = System.currentTimeMillis() - startTime;
    }

    private void backtrack(Stack<Position> stack) {
        Position position;
        while (this.noUnknownAdjacentCells()) {
            position = stack.pop();
            this.hp.memory[position.x][position.y] = "r"; // rollback
            this.hp.position = stack.peek().copy();
        }
    }

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

    private void goTo(Position target, Stack<Position> stack) {
        LinkedList<Position[]> shortestWay = new LinkedList<>();
        Queue<Position[]> queue = new LinkedList<>();

        this.updateBDFirstSearch();
        Position[] p = {this.hp.position, this.hp.position};
        queue.add(p);
        this.generateFear();
        boolean pathReliable = this.findShortestWay(target, shortestWay, queue);
        if (!pathReliable) {
            shortestWay = new LinkedList<>();
            queue = new LinkedList<>();

            this.updateBDFirstSearch();
            queue.add(p);
            this.goToDangerousArea(target, shortestWay, queue);
            Position newPos = shortestWay.get(shortestWay.size() - 2)[0].copy();
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
            return;
        }

        for (int i = shortestWay.size() - 2; i > -1; i--) {
            Position newPos = shortestWay.get(i)[0].copy();
            if (this.field.notEnemy(newPos)) {
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
            } else {
                break;
            }
        }
    }

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

    private void markUsefulRoute(Stack<Position> stack) {
        while (!stack.empty()) {
            Position position = stack.peek();
            this.hp.memory[position.x][position.y] = "u";
            stack.pop();
        }
        stack.push(this.hp.position.copy());
    }
}
