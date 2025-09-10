import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyByAddition(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyRecursive(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyByBitShift(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyByLogarithms(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void russianPeasantMultiplication(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyBySeries(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyByDivision(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyUsingPow(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyUsingArray(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }

    @ParameterizedTest
    @CsvSource({
            "6, 7, 42",
            "0, 4, 0",
            "4, 0, 0",
            "-6, 7, -42",
            "6, -7, -42",
            "-6, -7, 42",
            "8, 12, 96"
    })
    void multiplyUsingString(int a, int b, int expected) {
        assertEquals(expected, Main.multiplyByAddition(a, b));
    }
}