package test.com.project.unit.ui;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import java.lang.reflect.*;

import com.models.PolynomialFunction;
import com.models.Point;
import com.ui.Admin;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {
    private Admin admin;
    private JTextField tField;
    private JTextField pField;
    private JTextArea infoArea;
    private List<Point> points;

    @BeforeEach
    void setUp() throws Exception {
        admin = new Admin();

        // Получаем доступ к приватным полям через рефлексию
        Field tFieldField = Admin.class.getDeclaredField("tField");
        tFieldField.setAccessible(true);
        tField = (JTextField) tFieldField.get(admin);

        Field pFieldField = Admin.class.getDeclaredField("pField");
        pFieldField.setAccessible(true);
        pField = (JTextField) pFieldField.get(admin);

        Field infoAreaField = Admin.class.getDeclaredField("infoArea");
        infoAreaField.setAccessible(true);
        infoArea = (JTextArea) infoAreaField.get(admin);

        Field pointsField = Admin.class.getDeclaredField("points");
        pointsField.setAccessible(true);
        points = (List<Point>) pointsField.get(admin);
    }

    @Test
    void testAdminInitialization() {
        assertNotNull(admin);
        assertEquals("Админ панель", admin.getTitle());
        assertEquals(JFrame.DISPOSE_ON_CLOSE, admin.getDefaultCloseOperation());
    }

    @Test
    void testAddPointWithValidInput() throws Exception {
        // Подготовка
        tField.setText("10.5");
        pField.setText("20.3");

        // Выполнение
        invokePrivateMethod("addPoint");

        // Проверка
        assertEquals(1, points.size());
        assertEquals(10.5, points.get(0).t, 0.000001);
        assertEquals(20.3, points.get(0).p, 0.000001);
        assertTrue(infoArea.getText().contains("Точек: 1"));
    }

    @Test
    void testAddPointWithEmptyInput() throws Exception {
        // Подготовка
        tField.setText("");
        pField.setText("20.3");

        // Выполнение
        invokePrivateMethod("addPoint");

        // Проверка - не должна добавиться точка
        assertEquals(0, points.size());
    }

    @Test
    void testAddPointWithInvalidNumberFormat() throws Exception {
        // Подготовка
        tField.setText("not-a-number");
        pField.setText("20.3");

        // Выполнение
        invokePrivateMethod("addPoint");

        // Проверка
        assertEquals(0, points.size());
    }

    @Test
    void testClearPoints() throws Exception {
        // Подготовка - добавляем точки
        points.add(new Point(1, 2));
        points.add(new Point(3, 4));

        // Выполнение
        invokePrivateMethod("clearPoints");

        // Проверка
        assertEquals(0, points.size());
        assertTrue(infoArea.getText().contains("Точки очищены"));
    }

    @Test
    void testUpdatePointsInfo() throws Exception {
        // Подготовка
        points.add(new Point(10, 20));
        points.add(new Point(30, 40));

        // Выполнение
        invokePrivateMethod("updatePointsInfo");

        // Проверка
        String text = infoArea.getText();
        assertTrue(text.contains("Точек: 2"));
        assertTrue(text.contains("T=10.0"));
        assertTrue(text.contains("P=20.0"));
        assertTrue(text.contains("T=30.0"));
        assertTrue(text.contains("P=40.0"));
    }

    @Test
    void testFindBestFunctionWithInsufficientPoints() throws Exception {
        // Подготовка - меньше 3 точек
        points.add(new Point(1, 2));
        points.add(new Point(3, 4));

        // Выполнение
        invokePrivateMethod("findBestFunction");

        // Проверка
        assertTrue(infoArea.getText().contains("Мало точек"));
    }

    @Test
    void testCalculateError() throws Exception {
        // Создаем тестовую функцию
        PolynomialFunction function = new PolynomialFunction("Test",
                new double[]{1, 0, 0, 0, 0}); // y = 1

        // Создаем точки
        List<Point> testPoints = List.of(
                new Point(0, 1),   // точное совпадение
                new Point(1, 2),   // ошибка = 1
                new Point(2, 1)    // ошибка = 0
        );

        // Вызываем приватный метод
        Method method = Admin.class.getDeclaredMethod(
                "calculateError", PolynomialFunction.class, List.class);
        method.setAccessible(true);

        double error = (double) method.invoke(admin, function, testPoints);

        // Ожидаемая ошибка: (0 + 1 + 0) / 3 = 0.333...
        assertEquals(0.333333, error, 0.000001);
    }

    private void invokePrivateMethod(String methodName) {
        try {
            Method method = Admin.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(admin);
        } catch (Exception e) {
            fail("Не удалось вызвать приватный метод: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        // Очистка тестовых файлов
        File file = new File("selected_function.dat");
        if (file.exists()) {
            file.delete();
        }
    }
}