import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean active = true;
        Map<String, Long> pidMap = new HashMap<>();

        try (Scanner scanner = new Scanner(System.in)) {
            while (active) {
                active = menu(pidMap, scanner);
            }
        }
    }

    static boolean menu(Map<String, Long> pidMap, Scanner scanner) {
        System.out.println("\n======== APP MENU ========");
        System.out.println("|     1. calc            |");
        System.out.println("|     2. notepad         |");
        System.out.println("|     9. stop process    |");
        System.out.println("|     0. close           |");
        System.out.println("==========================");
        System.out.print("Input number or name to start: ");

        String choice = scanner.next();
        switch (choice) {
            case "1", "calc" -> {
                startProcess(pidMap, "calc.exe");
                return true;
            }
            case "2", "notepad" -> {
                startProcess(pidMap, "notepad.exe");
                return true;
            }
            case "9", "stop process" -> {
                return stopProcess(pidMap, scanner);
            }
            case "0", "close" -> {
                return closeProgram(scanner);
            }
            default -> {
                System.out.println("Error: Invalid choise, try again\n");
                return true;
            }
        }
    }

    private static void startProcess(Map<String, Long> pidMap, String name) {
        try {
            ProcessBuilder pb = new ProcessBuilder(name);
            Process process = pb.start();
            pidMap.put(name, process.pid());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean stopProcess(Map<String, Long> pidMap, Scanner scanner) {
        while (true) {
            System.out.println("Which process you want to stop?:");
            for (Map.Entry<String, Long> entry : pidMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.print("\nInput process name: ");

            String name = scanner.next();

            System.out.print("Do you want to close the process? (Y/n): ");

            String choice = scanner.next();
            switch (choice) {
                case "y", "Y", "":
                    try {
                        Runtime.getRuntime().exec("taskkill /F /PID " + pidMap.get(name));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    return true;
                case "n", "N":
                    return true;
                default:
                    System.out.println("Error: Invalid choice, try again");
            }
        }
    }

    private static boolean closeProgram(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to close the program? (Y/n): ");

            String choice = scanner.next();
            switch (choice) {
                case "y", "Y", "":
                    return false;
                case "n", "N":
                    return true;
                default:
                    System.out.println("Error: Invalid choice, try again");
            }
        }
    }
}