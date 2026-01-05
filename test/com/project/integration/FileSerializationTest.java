package test.com.project.integration;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;

import com.models.PolynomialFunction;

import static org.junit.jupiter.api.Assertions.*;

public class FileSerializationTest {
    private static final String TEST_FILE = "test_function.dat";

    @BeforeEach
    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testSerializationDeserialization() throws Exception {
        // Создаем тестовую функцию
        PolynomialFunction original = new PolynomialFunction(
                "Test Function",
                new double[]{1.1, 2.2, 3.3, 4.4, 5.5}
        );

        // Сериализуем
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TEST_FILE))) {
            oos.writeObject(original);
        }

        // Проверяем, что файл создан
        assertTrue(Files.exists(Paths.get(TEST_FILE)));

        // Десериализуем
        PolynomialFunction deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(TEST_FILE))) {
            deserialized = (PolynomialFunction) ois.readObject();
        }

        // Проверяем корректность
        assertNotNull(deserialized);
        assertEquals(original.getName(), deserialized.getName());
        assertArrayEquals(original.getCoefficients(),
                deserialized.getCoefficients(), 0.000001);
        assertEquals(original, deserialized);
    }

    @Test
    void testSerializationWithDifferentFunctions() throws Exception {
        // Тестируем все три функции из проекта
        PolynomialFunction[] functions = {
                new PolynomialFunction("Функция 1 (Зеленые точки)",
                        new double[]{-0.98979762, 0.11987753, -0.0017109931, 1.9754164e-005, -6.6718801e-008}),
                new PolynomialFunction("Функция 2 (Красные точки)",
                        new double[]{-1.0836957, 0.18584902, -0.0040785459, 4.6382046e-005, -1.7192525e-007}),
                new PolynomialFunction("Функция 3 (Синие точки)",
                        new double[]{10.384766, -0.90320964, 0.026917623, -0.00027985737, 9.8129281e-007})
        };

        for (int i = 0; i < functions.length; i++) {
            // Сериализуем
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(TEST_FILE))) {
                oos.writeObject(functions[i]);
            }

            // Десериализуем
            PolynomialFunction deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(TEST_FILE))) {
                deserialized = (PolynomialFunction) ois.readObject();
            }

            // Проверяем
            assertEquals(functions[i].getName(), deserialized.getName());
            assertArrayEquals(functions[i].getCoefficients(),
                    deserialized.getCoefficients(), 0.000000001);

            // Удаляем файл для следующей итерации
            Files.deleteIfExists(Paths.get(TEST_FILE));
        }
    }

    @Test
    void testFileNotFoundOnDeserialization() {
        assertThrows(FileNotFoundException.class, () -> {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("non_existent_file.dat"))) {
                ois.readObject();
            }
        });
    }

    @Test
    void testSerializationPerformance() throws Exception {
        PolynomialFunction function = new PolynomialFunction("Test",
                new double[]{1, 2, 3, 4, 5});

        long startTime = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(TEST_FILE))) {
                oos.writeObject(function);
            }

            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(TEST_FILE))) {
                ois.readObject();
            }
        }

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        System.out.println("Время 1000 сериализаций/десериализаций: " +
                String.format("%.2f", durationMs) + " мс");

        assertTrue(durationMs < 5000,
                "1000 операций должны занимать менее 5 секунд");
    }
}