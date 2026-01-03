package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import com.util.Log;

public class UserLogin extends JFrame {
    private static final Logger logger = Log.getLogger(UserLogin.class.getName());
    private final String userType;

    public UserLogin(String userType) {
        this.userType = userType;
        setTitle("Вход - " + (userType.equals("admin") ? "Администратор" : "Пользователь"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        logger.info("Открыто окно авторизации для " + userType);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel titleLabel = new JLabel("Вход для " + (userType.equals("admin") ? "администратора" : "пользователя"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel loginLabel = new JLabel("Логин:");
        JTextField loginField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Войти");
        JButton cancelButton = new JButton("Отмена");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel, gbc);
        mainPanel.add(loginLabel, gbc);
        mainPanel.add(loginField, gbc);
        mainPanel.add(passwordLabel, gbc);
        mainPanel.add(passwordField, gbc);
        mainPanel.add(buttonPanel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticate(login, password)) {
                    logger.info("Успешная авторизация для " + userType);
                    dispose();
                    if (userType.equals("admin")) {
                        new Admin().setVisible(true);
                    } else new User().setVisible(true);
                } else {
                    logger.warning("Неудачная попытка входа для " + userType);
                    JOptionPane.showMessageDialog(UserLogin.this,
                            "Неверный логин или пароль!",
                            "Ошибка авторизации",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Выход из окна входа");
                dispose();
            }
        });

        add(mainPanel);
    }

    private boolean authenticate(String login, String password) {
        if (userType.equals("admin")) {
            return "admin".equals(login) && "admin123".equals(password);
        } else {
            return "user".equals(login) && "user123".equals(password);
        }
    }
}