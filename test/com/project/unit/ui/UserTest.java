package test.com.project.unit.ui;

import org.junit.jupiter.api.*;
import java.io.*;
import javax.swing.*;
import java.lang.reflect.*;

import com.models.PolynomialFunction;
import com.ui.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;
    private JTextField inputField;
    private JLabel resultLabel;

    @BeforeEach
    void setUp() throws Exception {
        // Сначала создаем тестовый файл с функцией
        createTestFunctionFile();

        // Создаем пользователя
        user = new User();

        // Получаем доступ к приватным полям
        Field inputFieldField = User.class.getDeclaredField("inputField");
        inputFieldField.setAccessible(true);
        inputField = (JTextField) inputFieldField.get(user);

        Field resultLabelField = User.class.getDeclaredField("resultLabel");
        resultLabelField.setAccessible(true);
        resultLabel = (JLabel) resultLabelField.get(user);
    }

    @Test
    void testUserInitialization() {
        assertNotNull(user);
        assertEquals("Пользователь - Вычисление P по T", user.getTitle());
        assertFalse(user.isResizable());
    }

    @Test
    void testCalculateResultWithValidInput() throws Exception {
        // Подготовка
        inputField.setText("50.0");

        // Выполнение
        invokePrivateMethod("calculateResult");

        // Проверка
        String resultText = resultLabel.getText();
        assertTrue(resultText.contains("Результат:"));
        assertTrue(resultText.contains("P ="));
    }

    @Test
    void testCalculateResultWithInvalidInput() throws Exception {
        // Подготовка
        inputField.setText("not-a-number");

        // Выполнение
        invokePrivateMethod("calculateResult");

        // Проверка
        assertTrue(resultLabel.getText().contains("Ошибка"));
    }

    @Test
    void testCalculateResultWithEmptyInput() throws Exception {
        // Подготовка
        inputField.setText("");

        // Выполнение
        invokePrivateMethod("calculateResult");

        // Проверка
        assertTrue(resultLabel.getText().contains("Ошибка"));
    }

    @Test
    void testLoadFunctionWhenFileDoesNotExist() {
        // Удаляем тестовый файл
        new File("selected_function.dat").delete();

        // Создаем нового пользователя - должно показать ошибку
        User newUser = new User();
        assertNotNull(newUser);
        // Frame должен быть закрыт из-за ошибки загрузки
    }

    private void invokePrivateMethod(String methodName) {
        try {
            Method method = User.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(user);
        } catch (Exception e) {
            fail("Не удалось вызвать приватный метод: " + e.getMessage());
        }
    }

    private void createTestFunctionFile() throws IOException {
        PolynomialFunction testFunction = new PolynomialFunction("Тестовая функция",
                new double[]{-0.98979762, 0.11987753, -0.0017109931, 1.9754164e-005, -6.6718801e-008});

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("selected_function.dat"))) {
            oos.writeObject(testFunction);
        }
    }

    @AfterEach
    void tearDown() {
        // Очистка тестовых файлов
        File file = new File("selected_function.dat");
        if (file.exists()) {
            file.delete();
        }
        user.dispose();
    }
}