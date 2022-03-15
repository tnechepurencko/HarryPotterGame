import java.util.Scanner;

/*
coordinates examples:
2
1

[0,0] [0,5] [6,3] [7,5] [0,8] [3,5]
1
[0,0] [4,2] [7,3] [7,5] [0,8] [3,5]
1
[0,0] [0,5] [6,3] [0,8] [8,8] [3,5]
1
[0,0] [4,2] [7,3] [7,5] [8,0] [3,5]
1
[0,0] [4,2] [7,3] [8,1] [8,0] [3,5]
1
[0,0] [4,2] [7,3] [7,5] [8,1] [8,0]
1
[0,0] [0,5] [6,3] [0,8] [3,5] [3,5]
1
[0,0] [1,4] [5,3] [5,1] [7,5] [7,5]
1
[0,0] [1,4] [5,3] [0,0] [7,5] [7,5]
1
 */

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // TODO le нюанс: у котика красные глаза, у филча свечка, поэтому гарри их различает в библиотеке

        System.out.println("Do you wand to set positions of agents manually? (1-yes, 2-no)");
        String input = scanner.nextLine();
        while (input.compareTo("1") != 0 && input.compareTo("2") != 0) {
            System.out.println("Wrong format. Try again:");
            input = scanner.nextLine();
        }
        switch (input) {
            case "1" -> {
                System.out.println("Enter the coordinates, please (example of input: [0,0] [0,5] [6,3] [7,5] [0,8] [3,5])");
                String[][] coords = enterCoords();
                System.out.println("Choose the scenario, please (example of input: 1)");
                String scenario = enterScenario();

                Field field = new Field(coords, Integer.parseInt(scenario));
                System.out.println("Field:");
                field.print();
                System.out.println("Memory:");
                field.hp.printMemory();

                Backtracking backtracking = new Backtracking(field.hp, field);
                AStar aStar = new AStar(field.hp, field);

                backtracking.search(field);
                aStar.search(field);

                System.out.println();
                System.out.println("HARRY ENDS THE GAME TAKING " + backtracking.step + " STEPS USING BACKTRACKING ALGORITHM.");
                System.out.println("HARRY ENDS THE GAME TAKING " + aStar.step + " STEPS USING A_STAR ALGORITHM.");
            } case "2" -> {
                System.out.println("Choose the scenario, please (example of input: 1)");
                String scenario = scanner.nextLine();

                Field field = new Field(Integer.parseInt(scenario));
                field.print();

                Backtracking backtracking = new Backtracking(field.hp, field);
                AStar aStar = new AStar(field.hp, field);

                backtracking.search(field);
                aStar.search(field);

                System.out.println();
                System.out.println("HARRY ENDS THE GAME TAKING " + backtracking.step + " STEPS USING BACKTRACKING ALGORITHM.");
                System.out.println("HARRY ENDS THE GAME TAKING " + aStar.step + " STEPS USING A_STAR ALGORITHM.");
            }
        }
    }

    static String[][] enterCoords() {
        String coords;
        String[] step1;
        String[] step2;
        String[][] step3;
        boolean ok;

        while (true) {
            ok = true;
            coords = scanner.nextLine();
            step1 = coords.split(" ");
            if (step1.length != 6) {
                System.out.println("Wrong format. Try again:");
                continue;
            }

            for (int i = 0; i < 6; i++) {
                if (step1[i].length() != 5) {
                    ok = false;
                    break;
                }
            }
            if (!ok) {
                System.out.println("Wrong format. Try again:");
                continue;
            }

            for (int i = 0; i < 6; i++) {
                step1[i] = step1[i].substring(1, 4);
            }

            step3 = new String[6][2];
            for (int i = 0; i < 6; i++) {
                step2 = step1[i].split(",");
                if (step2.length != 2 || notCoordinate(step2[0]) || notCoordinate(step2[1])) {
                    ok = false;
                    break;
                }
                step3[i] = step2;

            }

            if (ok && !wrongCoords(step3)) {
                break;
            }
            System.out.println("Wrong format. Try again:");
        }
        return step3;
    }

    static boolean wrongCoords(String[][] coords) {
        boolean ok = false;
        Position position = new Position(Integer.parseInt(coords[1][0]), Integer.parseInt(coords[1][1]));
        Person observerF = new Person(position, "oF");
        position = new Position(Integer.parseInt(coords[2][0]), Integer.parseInt(coords[2][1]));
        Person observerN = new Person(position, "oN");

        if (coords[0][0].compareTo("0") != 0 || coords[0][1].compareTo("0") != 0) {
            System.out.println("Coordinate of Harry is wrong.");
            ok = true;
        }

        if (observerF.squarePerception(new Position(Integer.parseInt(coords[3][0]), Integer.parseInt(coords[3][1])), 2) ||
                observerN.squarePerception(new Position(Integer.parseInt(coords[3][0]), Integer.parseInt(coords[3][1])), 1)) {
            System.out.println("Coordinate of the book is wrong.");
            ok = true;
        }

        if (observerF.squarePerception(new Position(Integer.parseInt(coords[4][0]), Integer.parseInt(coords[4][1])), 2) ||
                observerN.squarePerception(new Position(Integer.parseInt(coords[4][0]), Integer.parseInt(coords[4][1])), 1)) {
            System.out.println("Coordinate of the cloak is wrong.");
            ok = true;
        }

        if (observerF.squarePerception(new Position(Integer.parseInt(coords[5][0]), Integer.parseInt(coords[5][1])), 2) ||
                observerN.squarePerception(new Position(Integer.parseInt(coords[5][0]), Integer.parseInt(coords[5][1])), 1)) {
            System.out.println("Coordinate of the exit is wrong.");
            ok = true;
        }
        return ok;
    }

    static String enterScenario() {
        String s;

        while (true) {
            s = scanner.nextLine();
            if (s.compareTo("1") == 0 || s.compareTo("2") == 0) {
                break;
            }
            System.out.println("Wrong format. Try again:");
        }
        return s;
    }

    static boolean notCoordinate(String s) {
        return s.compareTo("0") != 0 && s.compareTo("1") != 0 && s.compareTo("2") != 0 && s.compareTo("3") != 0 &&
                s.compareTo("4") != 0 && s.compareTo("5") != 0 && s.compareTo("6") != 0 && s.compareTo("7") != 0 &&
                s.compareTo("8") != 0;
    }
}
