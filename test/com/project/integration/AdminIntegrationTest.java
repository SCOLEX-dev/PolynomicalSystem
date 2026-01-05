package test.com.project.integration;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;
import java.lang.reflect.*;

import com.models.PolynomialFunction;
import com.models.Point;
import com.ui.Admin;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

public class AdminIntegrationTest {
    private File testFile;
    private Admin admin;

    @BeforeEach
    void setUp() {
        testFile = new File("selected_function.dat");
        if (testFile.exists()) {
            testFile.delete();
        }

        admin = new Admin();
    }

    @Test
    void testCompleteWorkflow() throws Exception {
        // 1. Добавляем точки
        addPointsToAdmin(new double[][]{{20, 1}, {40, 2}, {60, 3}});

        // 2. Находим лучшую функцию
        findBestFunction();

        // 3. Получаем выбранную функцию
        Field selectedFunctionField = Admin.class.getDeclaredField("selectedFunction");
        selectedFunctionField.setAccessible(true);
        PolynomialFunction selectedFunction = (PolynomialFunction) selectedFunctionField.get(admin);
        assertNotNull(selectedFunction);

        // 4. Сохраняем функцию
        saveFunction();

        // 5. Проверяем, что файл создан
        assertTrue(testFile.exists());

        // 6. Загружаем функцию напрямую из файла
        PolynomialFunction loadedFunction = loadFunctionFromFile();
        assertNotNull(loadedFunction);
        assertEquals(selectedFunction.getName(), loadedFunction.getName());

        // 7. Загружаем функцию через метод Admin
        loadSelectedFunction();
        PolynomialFunction reloadedFunction = (PolynomialFunction) selectedFunctionField.get(admin);
        assertNotNull(reloadedFunction);
    }

    @Test
    void testErrorHandlingWorkflow() throws Exception {
        // Добавляем точки, которые не подходят ни под одну функцию
        addPointsToAdmin(new double[][]{{1000, 1000}, {2000, 2000}, {3000, 3000}});

        // Пытаемся найти функцию - должна быть ошибка
        findBestFunction();

        // Проверяем, что кнопка сохранения отключена
        Field saveFunctionButtonField = Admin.class.getDeclaredField("saveFunctionButton");
        saveFunctionButtonField.setAccessible(true);
        JButton saveButton = (JButton) saveFunctionButtonField.get(admin);
        assertFalse(saveButton.isEnabled());
    }

    private void addPointsToAdmin(double[][] pointsArray) throws Exception {
        Field pointsField = Admin.class.getDeclaredField("points");
        pointsField.setAccessible(true);
        List<Point> points = (List<Point>) pointsField.get(admin);

        for (double[] point : pointsArray) {
            points.add(new Point(point[0], point[1]));
        }

        // Обновляем информацию о точках
        Method updateMethod = Admin.class.getDeclaredMethod("updatePointsInfo");
        updateMethod.setAccessible(true);
        updateMethod.invoke(admin);
    }

    private void findBestFunction() throws Exception {
        Method method = Admin.class.getDeclaredMethod("findBestFunction");
        method.setAccessible(true);
        method.invoke(admin);
    }

    private void saveFunction() throws Exception {
        Method method = Admin.class.getDeclaredMethod("saveFunctionSelection");
        method.setAccessible(true);
        method.invoke(admin);
    }

    private void loadSelectedFunction() throws Exception {
        Method method = Admin.class.getDeclaredMethod("loadSelectedFunction");
        method.setAccessible(true);
        method.invoke(admin);
    }

    private PolynomialFunction loadFunctionFromFile() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("selected_function.dat"))) {
            return (PolynomialFunction) ois.readObject();
        }
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
        admin.dispose();
    }
}