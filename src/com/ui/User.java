package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Logger;

import com.models.PolynomialFunction;
import com.util.Log;

public class User extends JFrame {
    private static final Logger logger = Log.getLogger(User.class.getName());

    private PolynomialFunction selectedFunction;
    private JTextField inputField;
    private JLabel resultLabel;

    // Диапазоны для каждой функции
    private static final double[][] FUNCTION_RANGES = {
            {20, 112},  // Зеленые точки
            {16, 120},  // Красные точки
            {20, 95}    // Синие точки
    };

    public User() {
        logger.info("Инициализация окна пользователя");
        setTitle("Пользователь - Вычисление P по T");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        loadSelectedFunction();
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JLabel titleLabel = new JLabel("Введите значение T", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        inputField = new JTextField();
        inputField.setHorizontalAlignment(JTextField.CENTER);

        JButton calculateButton = new JButton("Вычислить P");

        inputPanel.add(titleLabel);
        inputPanel.add(inputField);
        inputPanel.add(calculateButton);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultLabel = new JLabel("Результат: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultPanel.add(resultLabel, BorderLayout.CENTER);

        JTextArea infoArea = new JTextArea(4, 40);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        if (selectedFunction != null) {
            infoArea.setText("Загруженная функция:\n" +
                    selectedFunction.getName() + "\n\n" +
                    selectedFunction.getCoefficientsInfo());
            logger.info("Функция загружена: " + selectedFunction.getName());
        } else {
            infoArea.setText("Ошибка: функция не загружена!");
            logger.warning("Функция не была загружена");
        }

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(infoArea), BorderLayout.SOUTH);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });

        add(mainPanel);
        logger.fine("Окно пользователя инициализировано");
    }

    private void calculateResult() {
        if (selectedFunction == null) {
            logger.warning("Попытка вычисления без загруженной функции");
            resultLabel.setText("Ошибка: функция не загружена!");
            return;
        }

        try {
            double t = Double.parseDouble(inputField.getText());

            // Определяем диапазон в зависимости от выбранной функции
            double minT = 0, maxT = 0;
            String functionName = selectedFunction.getName();

            if (functionName.contains("Зеленые")) {
                minT = FUNCTION_RANGES[0][0];
                maxT = FUNCTION_RANGES[0][1];
            } else if (functionName.contains("Красные")) {
                minT = FUNCTION_RANGES[1][0];
                maxT = FUNCTION_RANGES[1][1];
            } else if (functionName.contains("Синие")) {
                minT = FUNCTION_RANGES[2][0];
                maxT = FUNCTION_RANGES[2][1];
            }

            // Проверяем, находится ли T в пределах диапазона
            if (t < minT || t > maxT) {
                logger.warning("Вычисления с помощью экстраполяции: T=" + t);
                JOptionPane.showMessageDialog(this,
                        "Вычисление P проведено с экстраполяцией\n" +
                                "(T вне диапазона [" + minT + ", " + maxT + "])",
                        "Информация",
                        JOptionPane.WARNING_MESSAGE);
            }

            double p = selectedFunction.calculate(t);

            logger.info("Вычисление успешно: T=" + t + " → P=" + p +
                    " (функция: " + selectedFunction.getName() + ")");

            resultLabel.setText(String.format("Результат: P = %.2f (при T = %.2f)", p, t));

        } catch (NumberFormatException ex) {
            logger.warning("Некорректный ввод: " + inputField.getText());
            resultLabel.setText("Ошибка: введите корректное число!");
        }
    }

    private void loadSelectedFunction() {
        File file = new File("selected_function.dat");
        if (!file.exists()) {
            logger.severe("Файл с функцией не найден: selected_function.dat");
            JOptionPane.showMessageDialog(this,
                    "Функция не выбрана администратором!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("selected_function.dat"))) {
            selectedFunction = (PolynomialFunction) ois.readObject();
            logger.info("Успешно загружена функция: " + selectedFunction.getName());

        } catch (FileNotFoundException e) {
            logger.severe("Файл с функцией не найден: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Функция не выбрана администратором!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Ошибка загрузки функции: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Ошибка при загрузке функции: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
}