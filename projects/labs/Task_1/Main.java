import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        boolean active = true;

        try (Scanner scanner = new Scanner(System.in)) {
            while (active) {
                active = menu(scanner);
            }
        }
    }

    static boolean menu(Scanner scanner) {
        System.out.println("\n======== APP MENU ========");
        System.out.println("|     1. paint           |");
        System.out.println("|     2. vivaldi         |");
        System.out.println("|     3. obsidian        |");
        System.out.println("|     4. qbittorrent     |");
        System.out.println("|     9. process info    |");
        System.out.println("|     0. close           |");
        System.out.println("==========================");
        System.out.print("Input number or name to start: ");

        String choice = scanner.next();
        switch (choice) {
            case "1", "paint" -> {
                startProcess("mspaint");
                return true;
            }
            case "2", "vivaldi" -> {
                startProcess(globalVariables.getPath("vivaldi"));
                return true;
            }
            case "3", "obsidian" -> {
                startProcess(globalVariables.getPath("obsidian"));
                return true;
            }
            case "4", "qbittorrent" -> {
                startProcess(globalVariables.getPath("qbittorrent"));
                return true;
            }
            case "9", "process info" -> {
                return processInfo(scanner);
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

    private static void startProcess(String name) {
        try {
            ProcessBuilder pb = new ProcessBuilder(name);
            Process process = pb.start();
            process.info();
            globalVariables.addProcess(name, process.pid());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean processInfo(Scanner scanner) {
        while (true) {
            System.out.println("List of the processes:");
            globalVariables.processOut();
            System.out.print("\nWhat do you want to do?");
            System.out.print("\n1. Delete the process");
            System.out.print("\n0. Close menu\n");

            String choice = scanner.next();
            switch (choice) {
                case "1": deleteProcess(scanner); return true;
                case "0": return true;
                default: System.out.println("Error: Invalid choice, try again");
            }
        }
    }

    private static void deleteProcess(Scanner scanner) {
        System.out.print("What process do you want to close?: ");
        Long index = scanner.nextLong();

        System.out.print("Do you want to close the process? (Y/n): ");

        String choice = scanner.next();
        switch (choice) {
            case "y", "Y", "":
                try {
                    Runtime.getRuntime().exec("taskkill /F /PID " + globalVariables.getPid(index));
                    globalVariables.delProcess(index);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            case "n", "N":
            default:
                System.out.println("Error: Invalid choice, try again");
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

    public class globalVariables {
        static Map<Long, String> pidMap = new HashMap<>();
        static Map<String, String> pathMap = Map.of(
                "vivaldi", "C:\\Users\\Skrpld\\AppData\\Local\\Vivaldi\\Application\\vivaldi.exe",
                "obsidian", "C:\\Users\\Skrpld\\AppData\\Local\\Programs\\Obsidian\\Obsidian.exe",
                "qbittorrent", "C:\\Program Files\\qBittorrent\\qbittorrent.exe"
        );
        static Map<Long, Long> indexes = new HashMap<>();

        public static String getPath(String name) {
            return pathMap.get(name);
        }

        public static Long getPid(Long i) {
            return indexes.get(i);
        }

        public static void addProcess(String name, Long pid) {
            pidMap.put(pid, name);
        }

        public static void delProcess(Long i) {
            pidMap.remove(getPid(i));
        }

        public static void processOut() {
            int i = 1;
            for (Map.Entry<Long, String> entry : pidMap.entrySet()) {
                indexes.put((long) i, entry.getKey());
                System.out.println(i + ". " + entry.getValue() + ":" + entry.getKey());
                i++;
            }
        }
    }
}