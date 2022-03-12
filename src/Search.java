import java.util.concurrent.TimeUnit;

public class Search {
    protected HarryPotter hp;

    public Search(HarryPotter hp) {
        this.hp = hp;
    }

    protected Position closestUnknown() {
        for (int radius = 1; radius < 8; radius++) {
            for (int i = this.hp.position.x - radius; i < this.hp.position.x + radius + 1; i++) {
                for (int j = this.hp.position.y - radius; j < this.hp.position.y + radius + 1; j++) {
                    if (i > -1 && i < 9 && j > -1 && j < 9) {
                        if (this.hp.memory[i][j].compareTo("·") == 0) {
                            return new Position(i, j);
                        }
                    }
                }
            }
        }
        return new Position(-1, -1);
    }

    protected void checkAndPrint(Field field, int stepNumber) {
        if (!this.hp.norrisFound) {
            this.hp.checkNorris(field);
        }
        if (!this.hp.filchFound) {
            this.hp.checkFilch(field);
        }

        this.hp.updateMemory(field);
        try {
            TimeUnit.MILLISECONDS.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("STEP " + stepNumber);
        System.out.println("Memory:");
        this.hp.printMemory();
    }


}
