import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== МЕТОДЫ УМНОЖЕНИЯ ===");
        System.out.println("1. Сложение в цикле");
        System.out.println("2. Рекурсивное сложение");
        System.out.println("3. Побитовый сдвиг");
        System.out.println("4. Логарифмический метод");
        System.out.println("5. Метод русского крестьянского умножения");
        System.out.println("6. Математический ряд");
        System.out.println("7. Метод деления");
        System.out.println("8. Использование Math.pow");
        System.out.println("9. Массив и сумма");
        System.out.println("10. String и повторение");

        System.out.print("Выберите метод (1-10): ");
        int choice = scanner.nextInt();

        System.out.print("Введите первое число: ");
        int a = scanner.nextInt();

        System.out.print("Введите второе число: ");
        int b = scanner.nextInt();

        int result = 0;
        long startTime = System.nanoTime();

        switch (choice) {
            case 1:
                result = multiplyByAddition(a, b);
                break;
            case 2:
                result = multiplyRecursive(a, b);
                break;
            case 3:
                result = multiplyByBitShift(a, b);
                break;
            case 4:
                result = multiplyByLogarithms(a, b);
                break;
            case 5:
                result = russianPeasantMultiplication(a, b);
                break;
            case 6:
                result = multiplyBySeries(a, b);
                break;
            case 7:
                result = multiplyByDivision(a, b);
                break;
            case 8:
                result = multiplyUsingPow(a, b);
                break;
            case 9:
                result = multiplyUsingArray(a, b);
                break;
            case 10:
                result = multiplyUsingString(a, b);
                break;
            default:
                System.out.println("Неверный выбор!");
                return;
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        System.out.println("Результат: " + a + " × " + b + " = " + result);
        System.out.println("Время выполнения: " + duration + " наносекунд");
    }

    // 1. Умножение через сложение в цикле
    public static int multiplyByAddition(int a, int b) {
        int result = 0;
        for (int i = 0; i < Math.abs(b); i++) {
            result += Math.abs(a);
        }
        return (a < 0 ^ b < 0) ? -result : result;
    }

    // 2. Рекурсивное умножение
    public static int multiplyRecursive(int a, int b) {
        if (b == 0) return 0;
        if (b > 0) return a + multiplyRecursive(a, b - 1);
        return -multiplyRecursive(a, -b);
    }

    // 3. Побитовый сдвиг (умножение на степени двойки)
    public static int multiplyByBitShift(int a, int b) {
        int result = 0;
        int absA = Math.abs(a);
        int absB = Math.abs(b);

        while (absB > 0) {
            if ((absB & 1) == 1) {
                result += absA;
            }
            absA <<= 1;
            absB >>= 1;
        }

        return (a < 0 ^ b < 0) ? -result : result;
    }

    // 4. Логарифмический метод
    public static int multiplyByLogarithms(int a, int b) {
        if (a == 0 || b == 0) return 0;
        double result = Math.exp(Math.log(Math.abs(a)) + Math.log(Math.abs(b)));
        return (int) Math.round((a < 0 ^ b < 0) ? -result : result);
    }

    // 5. Русское крестьянское умножение
    public static int russianPeasantMultiplication(int a, int b) {
        int result = 0;
        int x = Math.abs(a);
        int y = Math.abs(b);

        while (y > 0) {
            if (y % 2 == 1) {
                result += x;
            }
            x <<= 1;
            y >>= 1;
        }

        return (a < 0 ^ b < 0) ? -result : result;
    }

    // 6. Математический ряд
    public static int multiplyBySeries(int a, int b) {
        int result = 0;
        int n = Math.abs(b);
        for (int i = 0; i < n; i++) {
            result += a;
        }
        return (b < 0) ? -result : result;
    }

    // 7. Метод через деление
    public static int multiplyByDivision(int a, int b) {
        if (b == 0) return 0;
        return (int) (a / (1.0 / b));
    }

    // 8. Использование Math.pow
    public static int multiplyUsingPow(int a, int b) {
        if (a == 0 || b == 0) return 0;
        double result = Math.pow(10, Math.log10(Math.abs(a)) + Math.log10(Math.abs(b)));
        return (int) Math.round((a < 0 ^ b < 0) ? -result : result);
    }

    // 9. Использование массива и суммы
    public static int multiplyUsingArray(int a, int b) {
        int[] array = new int[Math.abs(b)];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.abs(a);
        }

        int sum = 0;
        for (int num : array) {
            sum += num;
        }

        return (a < 0 ^ b < 0) ? -sum : sum;
    }

    // 10. Использование String и повторения
    public static int multiplyUsingString(int a, int b) {
        if (b == 0) return 0;
        String repeated = "0".repeat(Math.abs(b) * Math.abs(a));
        int result = repeated.length() / Math.abs(b);
        return (a < 0 ^ b < 0) ? -result : result;
    }
}