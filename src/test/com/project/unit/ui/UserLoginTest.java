package test.com.project.unit.ui;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.lang.reflect.*;

import com.ui.UserLogin;

import static org.junit.jupiter.api.Assertions.*;

public class UserLoginTest {

    @Test
    void testAdminAuthentication() throws Exception {
        UserLogin userLogin = new UserLogin("admin");


        Method method = UserLogin.class.getDeclaredMethod(
                "authenticate", String.class, String.class);
        method.setAccessible(true);


        boolean result = (boolean) method.invoke(userLogin, "admin", "admin123");
        assertTrue(result);


        result = (boolean) method.invoke(userLogin, "admin", "wrong");
        assertFalse(result);

        result = (boolean) method.invoke(userLogin, "wrong", "admin123");
        assertFalse(result);

        userLogin.dispose();
    }

    @Test
    void testUserAuthentication() throws Exception {
        UserLogin userLogin = new UserLogin("user");

        Method method = UserLogin.class.getDeclaredMethod(
                "authenticate", String.class, String.class);
        method.setAccessible(true);

        // Корректные учетные данные
        boolean result = (boolean) method.invoke(userLogin, "user", "user123");
        assertTrue(result);

        // Неверные учетные данные
        result = (boolean) method.invoke(userLogin, "user", "wrong");
        assertFalse(result);

        userLogin.dispose();
    }

    @Test
    void testUserLoginInitialization() {
        // Тест для администратора
        UserLogin adminLogin = new UserLogin("admin");
        assertEquals("Вход - Администратор", adminLogin.getTitle());
        assertFalse(adminLogin.isResizable());
        adminLogin.dispose();

        // Тест для пользователя
        UserLogin userLogin = new UserLogin("user");
        assertEquals("Вход - Пользователь", userLogin.getTitle());
        assertFalse(userLogin.isResizable());
        userLogin.dispose();
    }
}