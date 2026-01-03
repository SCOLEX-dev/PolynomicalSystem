package test.com.project.unit.models;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.models.PolynomialFunction;
import java.io.*;

public class PolynomialFunctionTest {
    private PolynomialFunction function;
    private final double[] testCoefficients = {1.0, 2.0, 3.0, 4.0, 5.0};
    private final String testName = "Test Function";

    @BeforeEach
    void setUp() {
        function = new PolynomialFunction(testName, testCoefficients);
    }

    @Test
    void testConstructor() {
        assertNotNull(function);
        assertEquals(testName, function.getName());
        assertArrayEquals(testCoefficients, function.getCoefficients(), 0.000001);
    }

    @Test
    void testConstructorWithInvalidCoefficients() {
        // Тест с null коэффициентами
        assertThrows(IllegalArgumentException.class, () -> {
            new PolynomialFunction("Test", null);
        });

        // Тест с неправильным количеством коэффициентов
        assertThrows(IllegalArgumentException.class, () -> {
            new PolynomialFunction("Test", new double[]{1, 2, 3});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PolynomialFunction("Test", new double[]{1, 2, 3, 4, 5, 6});
        });
    }

    @Test
    void testCalculate() {
        // Тест с x = 0
        double result = function.calculate(0);
        assertEquals(1.0, result, 0.000001);

        // Тест с x = 1
        result = function.calculate(1);
        double expected = 1.0 + 2.0*1 + 3.0*1 + 4.0*1 + 5.0*1;
        assertEquals(expected, result, 0.000001);

        // Тест с x = 2
        result = function.calculate(2);
        expected = 1.0 + 2.0*2 + 3.0*4 + 4.0*8 + 5.0*16;
        assertEquals(expected, result, 0.000001);

        // Тест с отрицательным значением
        result = function.calculate(-1);
        expected = 1.0 + 2.0*(-1) + 3.0*1 + 4.0*(-1) + 5.0*1;
        assertEquals(expected, result, 0.000001);

        // Тест с дробным значением
        result = function.calculate(0.5);
        expected = 1.0 + 2.0*0.5 + 3.0*0.25 + 4.0*0.125 + 5.0*0.0625;
        assertEquals(expected, result, 0.000001);
    }

    @Test
    void testGetCoefficientsInfo() {
        String info = function.getCoefficientsInfo();
        assertNotNull(info);
        assertTrue(info.contains("y ="));
        assertTrue(info.contains("a ="));
        assertTrue(info.contains("b ="));
        assertTrue(info.contains("c ="));
        assertTrue(info.contains("d ="));
        assertTrue(info.contains("e ="));

        // Проверяем форматирование
        assertTrue(info.contains("1.00000000"));
        assertTrue(info.contains("2.00000000"));
        assertTrue(info.contains("3.00000000"));
        assertTrue(info.contains("4.00000000"));
        assertTrue(info.contains("5.00000000"));
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        // Сериализация
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(function);
        oos.close();

        // Десериализация
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        PolynomialFunction deserialized = (PolynomialFunction) ois.readObject();

        // Проверка
        assertEquals(function.getName(), deserialized.getName());
        assertArrayEquals(function.getCoefficients(), deserialized.getCoefficients(), 0.000001);
    }

    @Test
    void testEqualsAndHashCode() {
        PolynomialFunction sameFunction = new PolynomialFunction(testName, testCoefficients);
        PolynomialFunction differentFunction = new PolynomialFunction("Different", new double[]{0,0,0,0,0});

        // Проверяем equals
        assertEquals(function, sameFunction);
        assertNotEquals(function, differentFunction);
        assertNotEquals(function, null);
        assertNotEquals(function, new Object());

        // Проверяем hashCode
        assertEquals(function.hashCode(), sameFunction.hashCode());
        assertNotEquals(function.hashCode(), differentFunction.hashCode());

        // Проверяем небольшие различия в коэффициентах
        PolynomialFunction slightlyDifferent = new PolynomialFunction(testName,
                new double[]{1.0, 2.0, 3.0, 4.0, 5.0000001});
        assertNotEquals(function, slightlyDifferent);
    }

    @Test
    void testToString() {
        String stringRepresentation = function.toString();
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains(testName));
        assertTrue(stringRepresentation.contains("coefficients="));
        assertTrue(stringRepresentation.contains("1.0"));
    }

    @Test
    void testGetCoefficientsReturnsCopy() {
        double[] coefficients = function.getCoefficients();
        coefficients[0] = 999.0; // Модифицируем копию

        // Оригинальные коэффициенты не должны измениться
        assertNotEquals(999.0, function.getCoefficients()[0], 0.000001);
        assertEquals(1.0, function.getCoefficients()[0], 0.000001);
    }
}