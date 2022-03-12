import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Do you wand to set positions of agents manually? (1-yes, 2-no)");
        String input = scanner.nextLine();
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

                if (!field.inputCorrect()) {
                    return;
                }

                Backtracking backtracking = new Backtracking(field.hp);
                AStar aStar = new AStar(field.hp);

//                backtracking.search(field);
                aStar.search(field);
            }
            case "2" -> {
                System.out.println("Choose the scenario, please (example of input: 1)");
                String scenario = scanner.nextLine();

                Field field = new Field(Integer.parseInt(scenario));
                field.print();
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

            if (ok) {
                break;
            }
            System.out.println("Wrong format. Try again:");
        }
        return step3;
    }

    static String enterScenario() {
        String s;

        while (true) {
            s = scanner.nextLine();
            if (isScenario(s)) {
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

    static boolean isScenario(String s) {
        return s.compareTo("1") == 0 || s.compareTo("2") == 0;
    }

}
