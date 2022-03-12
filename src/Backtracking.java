import java.util.Stack;

public class Backtracking extends Search {
    public Backtracking(HarryPotter hp) {
        super(hp);
    }

    public void search(Field field) {
        Stack<Position> stack = new Stack<>();
        stack.push(this.hp.position.copy());
        int step = 0;

        while (!this.hp.exitLibrary) {
            step++;
            if (this.hp.hasBook) {
                if (this.hp.position.equals(field.exit)) {
                    this.hp.exitLibrary = true;
                    System.out.println("YOU WON");
                } else {
                    goTo(field.exit);
                    stack.push(this.hp.position.copy());
                    getItem(field, stack);
                }
            } else if (!this.hasUnknownAdjacentCells() && !stack.empty() && this.canGoBack()) {
                this.backtrack(stack);
                this.hp.updateMemory(field);
                continue;
            } else {
                Position position = this.closestUnknown();
                goTo(position);
                stack.push(this.hp.position.copy());
                getItem(field, stack);
            }

            this.checkAndPrint(field, step);
        }
    }

    private void backtrack(Stack<Position> stack) {
        Position position = stack.pop();
        this.hp.memory[position.x][position.y] = "☒";
        this.hp.position = stack.peek().copy();
    }

    private boolean hasUnknownAdjacentCells() {
        for (int i = this.hp.position.x - 1; i < this.hp.position.x + 2; i++) {
            for (int j = this.hp.position.y - 1; j < this.hp.position.y + 2; j++) {
                if (i > -1 && i < 9 && j > -1 && j < 9) {
                    if (this.hp.memory[i][j].compareTo("·") == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canGoBack() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.hp.memory[i][j].compareTo("x") == 0) {
                    for (int i1 = i - 1; i1 < i + 2; i1++) {
                        for (int j1 = j - 1; j1 < j + 2; j1++) {
                            if (i1 > -1 && i1 < 9 && j1 > -1 && j1 < 9) {
                                if (this.hp.memory[i][j].compareTo("·") == 0) {
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

    private void goTo(Position position) {
        this.hp.memory[this.hp.position.x][this.hp.position.y] = "x";

        if (position.upper(this.hp.position)) {
            if (position.leftward(this.hp.position)) {
                if (this.hp.canMoveUL()) {
                    this.hp.moveUL();
                } else if (this.hp.canMoveLeft()) {
                    this.hp.moveLeft();
                } else if (this.hp.canMoveUp()) {
                    this.hp.moveUp();
                }
            } else if (position.rightward(this.hp.position)) {
                if (this.hp.canMoveUR()) {
                    this.hp.moveUR();
                } else if (this.hp.canMoveRight()) {
                    this.hp.moveRight();
                } else if (this.hp.canMoveUp()) {
                    this.hp.moveUp();
                }
            } else if (this.hp.canMoveUp()) {
                this.hp.moveUp();
            }
        } else if (position.downer(this.hp.position)) {
            if (position.leftward(this.hp.position)) {
                if (this.hp.canMoveDL()) {
                    this.hp.moveDL();
                } else if (this.hp.canMoveLeft()) {
                    this.hp.moveLeft();
                } else if (this.hp.canMoveDown()) {
                    this.hp.moveDown();
                }
            } else if (position.rightward(this.hp.position)) {
                if (this.hp.canMoveRD()) {
                    this.hp.moveRD();
                } else if (this.hp.canMoveRight()) {
                    this.hp.moveRight();
                } else if (this.hp.canMoveDown()) {
                    this.hp.moveDown();
                }
            } else if (this.hp.canMoveDown()) {
                this.hp.moveDown();
            }
        } else {
            if (position.leftward(this.hp.position)) {
                if (this.hp.canMoveLeft()) {
                    this.hp.moveLeft();
                }
            } else if (position.rightward(this.hp.position)) {
                if (this.hp.canMoveRight()) {
                    this.hp.moveRight();
                }
            }
        }
    }

    private void markUsefulRoute(Stack<Position> stack) {
        while (!stack.empty()) {
            Position position = stack.peek();
            this.hp.memory[position.x][position.y] = "u";
            stack.pop();
        }
        stack.push(this.hp.position.copy());
    }

    private void getItem(Field field, Stack<Position> stack) {
        if (this.hp.position.equals(field.book.position)) {
            field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasBook = true;
            System.out.println("BOOK FOUND");
            this.markUsefulRoute(stack);
        } else if (this.hp.position.equals(field.cloak.position)) {
            field.scheme[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.memory[this.hp.position.x][this.hp.position.y] = "·";
            this.hp.hasCloak = true;
            System.out.println("CLOAK FOUND");
            this.markUsefulRoute(stack);
        }
    }
}
