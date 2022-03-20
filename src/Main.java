import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
/*
coordinates examples:
2
2
1
[0,0] [0,5] [6,3] [7,5] [8,8] [3,5]
1
1
[0,0] [4,2] [7,3] [7,5] [0,8] [3,5]
1
[0,0] [4,2] [7,3] [7,5] [8,1] [8,0]
1
[0,0] [0,5] [6,3] [0,8] [3,5] [3,5]
1
[0,0] [1,4] [5,3] [5,1] [7,5] [7,5]
1
[0,0] [1,4] [5,3] [0,0] [7,5] [7,5]
1
[0,0] [3,1] [8,6] [2,5] [5,8] [7,2]
1
[0,0] [4,2] [0,5] [8,8] [7,7] [8,7]
1
[0,0] [3,3] [0,7] [0,4] [0,5] [7,1]
1
 */
public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        statistics();
//
//        System.out.println("Do you wand to set positions of agents manually? (1-yes, 2-no)");
//        String input = scanner.nextLine();
//        while (input.compareTo("1") != 0 && input.compareTo("2") != 0) {
//            System.out.println("Wrong format. Try again:");
//            input = scanner.nextLine();
//        }
//        switch (input) {
//            case "1" -> {
//                System.out.println("Enter the coordinates, please (example of input: [0,0] [0,5] [6,3] [7,5] [0,8] [3,5])");
//                String[][] coords = enterCoords();
//                System.out.println("Choose the scenario, please (example of input: 1)");
//                String scenario = enterScenario();
//
//                Field field = new Field(coords, Integer.parseInt(scenario));
//                launchAlgorithms(field);
//            } case "2" -> {
//                System.out.println("Choose the scenario, please (example of input: 1)");
//                String scenario = scanner.nextLine();
//
//                Field field = new Field(Integer.parseInt(scenario));
//                launchAlgorithms(field);
//            }
//        }
    }

    /**
     * The method collects statistics, calculates averages and medians.
     */
    static void statistics() {
        LinkedList<Long> statisticsTimeB1 = new LinkedList<>();
        LinkedList<Integer> statisticsStepB1 = new LinkedList<>();
        LinkedList<Long> statisticsTimeB2 = new LinkedList<>();
        LinkedList<Integer> statisticsStepB2 = new LinkedList<>();
        LinkedList<Long> statisticsTimeA1 = new LinkedList<>();
        LinkedList<Integer> statisticsStepA1 = new LinkedList<>();
        LinkedList<Long> statisticsTimeA2 = new LinkedList<>();
        LinkedList<Integer> statisticsStepA2 = new LinkedList<>();

        double avgTimeB1 = 0;
        double avgStepB1 = 0;
        double avgTimeA1 = 0;
        double avgStepA1 = 0;
        double avgTimeB2 = 0;
        double avgStepB2 = 0;
        double avgTimeA2 = 0;
        double avgStepA2 = 0;

        Field field1;
        Field field2;
        Backtracking backtracking1;
        Backtracking backtracking2;
        AStar aStar1;
        AStar aStar2;

        int count = 0;
        do {
            field1 = new Field(1);
            field2 = new Field(2);

            backtracking1 = new Backtracking(field1.hp, field1);
            aStar1 = new AStar(field1.hp, field1);
            backtracking2 = new Backtracking(field2.hp, field2);
            aStar2 = new AStar(field2.hp, field2);

            backtracking1.search();
            if (backtracking1.runtime == 0) {
                continue;
            }

            backtracking2.search();
            if (backtracking2.runtime == 0) {
                continue;
            }

            aStar1.search();
            aStar2.search();

            statisticsStepB1.add(backtracking1.step);
            statisticsTimeB1.add(backtracking1.runtime);
            statisticsStepA1.add(aStar1.step);
            statisticsTimeA1.add(aStar1.runtime);

            statisticsStepB2.add(backtracking2.step);
            statisticsTimeB2.add(backtracking2.runtime);
            statisticsStepA2.add(aStar2.step);
            statisticsTimeA2.add(aStar2.runtime);
            count++;
        } while (count != 100);

        System.out.println();
        System.out.print("BACKTRACKING (1st scenario): ");
        for (int i = 0; i < 100; i++) {
            avgStepB1 += statisticsStepB1.get(i);
            avgTimeB1 += ((double) statisticsTimeB1.get(i)) / 1000;
            System.out.print("(" + statisticsStepB1.get(i) + " steps / " + statisticsTimeB1.get(i) + " ns) ");
        }
        avgStepB1 /= 100;
        avgTimeB1 /= 100;

        System.out.println();
        System.out.print("A_STAR (1st scenario): ");
        for (int i = 0; i < 100; i++) {
            avgStepA1 += statisticsStepA1.get(i);
            avgTimeA1 += ((double) statisticsTimeA1.get(i)) / 1000;
            System.out.print("(" + statisticsStepA1.get(i) + " steps / " + statisticsTimeA1.get(i) + " ns) ");
        }
        avgStepA1 /= 100;
        avgTimeA1 /= 100;

        System.out.println();
        System.out.print("BACKTRACKING (2nd scenario): ");
        for (int i = 0; i < 100; i++) {
            avgStepB2 += statisticsStepB2.get(i);
            avgTimeB2 += ((double) statisticsTimeB2.get(i)) / 1000;
            System.out.print("(" + statisticsStepB2.get(i) + " steps / " + statisticsTimeB2.get(i) + " ns) ");
        }
        avgStepB2 /= 100;
        avgTimeB2 /= 100;

        System.out.println();
        System.out.print("A_STAR (2nd scenario): ");
        for (int i = 0; i < 100; i++) {
            avgStepA2 += statisticsStepA2.get(i);
            avgTimeA2 += ((double) statisticsTimeA2.get(i)) / 1000;
            System.out.print("(" + statisticsStepA2.get(i) + " steps / " + statisticsTimeA2.get(i) + " ns) ");
        }
        avgStepA2 /= 100;
        avgTimeA2 /= 100;
        System.out.println("\n");
        System.out.println("avg: B1(steps/time)  = (" + avgStepB1 + "/" + avgTimeB1 + "), " +
                "A1(steps/time)  = (" + avgStepA1 + "/" + avgTimeA1 + "), " +
                "B2(steps/time)  = (" + avgStepB2 + "/" + avgTimeB2 + "), " +
                "A2(steps/time)  = (" + avgStepA2 + "/" + avgTimeA2 + ")");

        Comparator<Integer> c = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };

        Comparator<Long> l = new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (((int) (o1 - o2)));
            }
        };

        statisticsStepA1.sort(c);
        statisticsStepA2.sort(c);
        statisticsTimeA1.sort(l);
        statisticsTimeA2.sort(l);
        statisticsStepB1.sort(c);
        statisticsStepB2.sort(c);
        statisticsTimeB1.sort(l);
        statisticsTimeB2.sort(l);

        System.out.println();
        System.out.print("BACKTRACKING (1st scenario): ");
        for (int i = 0; i < 100; i++) {
            System.out.print("(" + statisticsStepB1.get(i) + " steps / " + statisticsTimeB1.get(i) + " ns) ");
        }

        System.out.println();
        System.out.print("A_STAR (1st scenario): ");
        for (int i = 0; i < 100; i++) {
            System.out.print("(" + statisticsStepA1.get(i) + " steps / " + statisticsTimeA1.get(i) + " ns) ");
        }

        System.out.println();
        System.out.print("BACKTRACKING (2nd scenario): ");
        for (int i = 0; i < 100; i++) {
            System.out.print("(" + statisticsStepB2.get(i) + " steps / " + statisticsTimeB2.get(i) + " ns) ");
        }

        System.out.println();
        System.out.print("A_STAR (2nd scenario): ");
        for (int i = 0; i < 100; i++) {
            System.out.print("(" + statisticsStepA2.get(i) + " steps / " + statisticsTimeA2.get(i) + " ns) ");
        }

        System.out.println("\n");
        System.out.println("medians: B1(steps/time)  = (" + ((statisticsStepB1.get(49) + statisticsStepB1.get(50)) / 2) +
                "/" + ((statisticsTimeB1.get(49) + statisticsTimeB1.get(50)) / 2) + "), " +
                "A1(steps/time)  = (" + ((statisticsStepA1.get(49) + statisticsStepA1.get(50)) / 2) + "/" +
                ((statisticsTimeA1.get(49) + statisticsTimeA1.get(50)) / 2) + "), " +
                "B2(steps/time)  = (" + ((statisticsStepB2.get(49) + statisticsStepB2.get(50)) / 2) + "/" +
                ((statisticsTimeB2.get(49) + statisticsTimeB2.get(50)) / 2) + "), " +
                "A2(steps/time)  = (" + ((statisticsStepA2.get(49) + statisticsStepA2.get(50)) / 2) + "/" +
                ((statisticsTimeA2.get(49) + statisticsTimeA2.get(50)) / 2) + ")");

    }

    /**
     * The method launches the algorithms & provides us with the results of the algorithms.
     * @param field : current field
     */
    static void launchAlgorithms(Field field) {
        System.out.println("Field:");
        field.print();
        System.out.println("Memory:");
        field.hp.printMemory();

        Backtracking backtracking = new Backtracking(field.hp, field);
        AStar aStar = new AStar(field.hp, field);

        backtracking.search();
        aStar.search();

        System.out.println();
        System.out.print("HARRY " + backtracking.result + " THE GAME TAKING " + backtracking.step +
                " STEPS USING BACKTRACKING ALGORITHM.\nPATH: ");
        for (int i = 0; i < backtracking.path.size(); i++) {
            System.out.print("[" + backtracking.path.get(i).x + "," + backtracking.path.get(i).y + "] ");
        }
        System.out.println(".\nRUNTIME (WITH PRINT TIME): " + backtracking.runtime + " ns.");

        System.out.println("\n");
        System.out.print("HARRY " + aStar.result + " THE GAME TAKING " + aStar.step +
                " STEPS USING A_STAR ALGORITHM.\nPATH: ");
        for (int i = 0; i < aStar.path.size(); i++) {
            System.out.print("[" + aStar.path.get(i).x + "," + aStar.path.get(i).y + "] ");
        }
        System.out.println(".\nRUNTIME (WITH PRINT TIME): " + aStar.runtime + " ns.");
    }

    /**
     * The method gets the input, checks if it is correct, & splits it into the separate coordinates.
     * @return two-dimensional array with the coordinates
     */
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

    /**
     * @param coords : input coordinates
     * @return if the coordinated comply with the assignment rules.
     */
    static boolean wrongCoords(String[][] coords) {
        boolean ok = false;
        Position position = new Position(Integer.parseInt(coords[1][0]), Integer.parseInt(coords[1][1]));
        Inspector observerF = new Inspector(2, position, "oF");
        position = new Position(Integer.parseInt(coords[2][0]), Integer.parseInt(coords[2][1]));
        Inspector observerN = new Inspector(1, position, "oN");

        if (coords[0][0].compareTo("0") != 0 || coords[0][1].compareTo("0") != 0) {
            System.out.println("Coordinate of Harry is wrong.");
            ok = true;
        }

        position = new Position(Integer.parseInt(coords[3][0]), Integer.parseInt(coords[3][1]));
        if (observerF.seeItem(position) || observerN.seeItem(position)) {
            System.out.println("Coordinate of the book is wrong.");
            ok = true;
        }

        position = new Position(Integer.parseInt(coords[4][0]), Integer.parseInt(coords[4][1]));
        if (observerF.seeItem(position) || observerN.seeItem(position)) {
            System.out.println("Coordinate of the cloak is wrong.");
            ok = true;
        }

        position = new Position(Integer.parseInt(coords[5][0]), Integer.parseInt(coords[5][1]));
        if (observerF.seeItem(position) || observerN.seeItem(position)) {
            System.out.println("Coordinate of the exit is wrong.");
            ok = true;
        }
        return ok;
    }

    /**
     * @return : the scenario
     */
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

    /**
     * @param s : a string
     * @return if this string can be the abscissa of the ordinate of the current field.
     */
    static boolean notCoordinate(String s) {
        return s.compareTo("0") != 0 && s.compareTo("1") != 0 && s.compareTo("2") != 0 && s.compareTo("3") != 0 &&
                s.compareTo("4") != 0 && s.compareTo("5") != 0 && s.compareTo("6") != 0 && s.compareTo("7") != 0 &&
                s.compareTo("8") != 0;
    }
}
