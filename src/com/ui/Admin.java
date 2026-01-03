package com.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.models.PolynomialFunction;
import com.models.Point;
import com.util.Log;

public class Admin extends JFrame {
        private static final Logger logger = Log.getLogger(Admin.class.getName());

        private PolynomialFunction selectedFunction;
        private JTextField tField;
        private JTextField pField;
        private JButton addPointButton;
        private JButton findFunctionButton;
        private JButton saveFunctionButton;
        private JButton clearPointsButton;
        private JTextArea infoArea;
        private List<Point> points = new ArrayList<>();

        private final PolynomialFunction[] functions = {
                new PolynomialFunction("Функция 1 (Зеленые точки)",
                        new double[]{-0.98979762, 0.11987753, -0.0017109931, 1.9754164e-005, -6.6718801e-008}),
                new PolynomialFunction("Функция 2 (Красные точки)",
                        new double[]{-1.0836957, 0.18584902, -0.0040785459, 4.6382046e-005, -1.7192525e-007}),
                new PolynomialFunction("Функция 3 (Синие точки)",
                        new double[]{10.384766, -0.90320964, 0.026917623, -0.00027985737, 9.8129281e-007})
        };

        private static final double ERROR_THRESHOLD = 10000000.0;

        public Admin() {
                logger.info("Открытие админ панели");
                setTitle("Админ панель");
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setSize(700, 500);
                setLocationRelativeTo(null);
                initComponents();
                loadSelectedFunction();
        }

        private void initComponents() {
                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Панель ввода
                JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                inputPanel.setBorder(BorderFactory.createTitledBorder("Ввод точек"));

                JLabel tLabel = new JLabel("T:");
                tField = new JTextField();
                JLabel pLabel = new JLabel("P:");
                pField = new JTextField();

                inputPanel.add(tLabel);
                inputPanel.add(tField);
                inputPanel.add(pLabel);
                inputPanel.add(pField);

                // Кнопки для точек
                JPanel pointsButtonPanel = new JPanel(new FlowLayout());
                addPointButton = new JButton("Добавить точку");
                clearPointsButton = new JButton("Очистить точки");

                pointsButtonPanel.add(addPointButton);
                pointsButtonPanel.add(clearPointsButton);

                // Основные кнопки
                JPanel mainButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
                findFunctionButton = new JButton("Подобрать функцию");
                saveFunctionButton = new JButton("Сохранить функцию");

                mainButtonPanel.add(findFunctionButton);
                mainButtonPanel.add(saveFunctionButton);

                // Информационная панель
                JPanel infoPanel = new JPanel(new BorderLayout());
                infoPanel.setBorder(BorderFactory.createTitledBorder("Информация"));

                infoArea = new JTextArea(8, 40);
                infoArea.setEditable(false);
                JScrollPane infoScroll = new JScrollPane(infoArea);
                infoPanel.add(infoScroll, BorderLayout.CENTER);

                // Компоновка
                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.add(inputPanel, BorderLayout.CENTER);
                topPanel.add(pointsButtonPanel, BorderLayout.SOUTH);

                mainPanel.add(topPanel, BorderLayout.NORTH);
                mainPanel.add(mainButtonPanel, BorderLayout.CENTER);
                mainPanel.add(infoPanel, BorderLayout.SOUTH);

                // Обработчики событий
                addPointButton.addActionListener(e -> addPoint());
                clearPointsButton.addActionListener(e -> clearPoints());
                findFunctionButton.addActionListener(e -> findBestFunction());
                saveFunctionButton.addActionListener(e -> saveFunctionSelection());

                tField.addActionListener(e -> addPoint());
                pField.addActionListener(e -> addPoint());

                add(mainPanel);
        }

        private void addPoint() {
                try {
                        String tText = tField.getText().trim();
                        String pText = pField.getText().trim();

                        if (tText.isEmpty() || pText.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Заполните оба поля", "Ошибка", JOptionPane.WARNING_MESSAGE);
                                return;
                        }

                        double t = Double.parseDouble(tText);
                        double p = Double.parseDouble(pText);

                        points.add(new Point(t, p));
                        logger.info("Добавлена точка: T=" + t + ", P=" + p);

                        updatePointsInfo();

                        tField.setText("");
                        pField.setText("");
                        tField.requestFocus();

                } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Введите числа", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
        }

        private void clearPoints() {
                points.clear();
                logger.info("Точки очищены");
                infoArea.setText("Точки очищены. Введите новые значения.");
                saveFunctionButton.setEnabled(false);
        }

        private void updatePointsInfo() {
                infoArea.setText("Точек: " + points.size() + "\nНужно минимум 3\n\nТочки:");

                for (int i = 0; i < points.size(); i++) {
                        Point pt = points.get(i);
                        infoArea.append("\n" + (i+1) + ". T=" + pt.t + ", P=" + pt.p);
                }
        }

        private void findBestFunction() {
                if (points.size() < 3) {
                        infoArea.setText("Мало точек! Нужно минимум 3, а есть " + points.size());
                        return;
                }

                logger.info("Поиск функции для " + points.size() + " точек");

                PolynomialFunction bestFunction = null;
                double minError = Double.MAX_VALUE;
                String result = "Результаты:\n\n";

                for (PolynomialFunction function : functions) {
                        double error = calculateError(function, points);

                        if (error < minError) {
                                minError = error;
                                bestFunction = function;
                        }
                }

                if (bestFunction != null && minError <= ERROR_THRESHOLD) {
                        selectedFunction = bestFunction;

                        result += "\nЛучшая функция: " + bestFunction.getName();
                        result += "\nСредняя ошибка: " + String.format("%.6f", minError);

                        infoArea.setText(result);
                        saveFunctionButton.setEnabled(true);

                } else {
                        result += "\nОШИБКА: Все функции имеют большую ошибку: " + String.format("%.6f", minError) +
                                "\nПорог: " + ERROR_THRESHOLD +
                                "\n\nВведите новые точки";

                        infoArea.setText(result);

                        JOptionPane.showMessageDialog(this,
                                "Некорректные данные! Введите новые точки.",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);

                        int choice = JOptionPane.showConfirmDialog(this,
                                "Очистить точки и начать заново?",
                                "Очистка",
                                JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                                clearPoints();
                        }

                        saveFunctionButton.setEnabled(false);
                }
        }

        private double calculateError(PolynomialFunction function, List<Point> points) {
                double totalError = 0;
                for (Point point : points) {
                        double calculatedP = function.calculate(point.t);
                        totalError += Math.abs(calculatedP - point.p);
                }
                return totalError / points.size();
        }

        private void saveFunctionSelection() {
                if (selectedFunction == null) {
                        JOptionPane.showMessageDialog(this, "Сначала подберите функцию!", "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                }

                try (ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream("selected_function.dat"))) {
                        oos.writeObject(selectedFunction);
                        logger.info("Функция сохранена: " + selectedFunction.getName());

                        infoArea.setText(infoArea.getText() + "\n\nФункция сохранена!");

                        JOptionPane.showMessageDialog(this,
                                "Функция сохранена!",
                                "Сохранение",
                                JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Ошибка сохранения: " + ex.getMessage(),
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                }
        }

        private void loadSelectedFunction() {
                File file = new File("selected_function.dat");
                if (file.exists()) {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream("selected_function.dat"))) {

                                selectedFunction = (PolynomialFunction) ois.readObject();
                                infoArea.setText("Загружена функция:\n" + selectedFunction.getName() +
                                        "\n\nКоэффициенты:\n" + selectedFunction.getCoefficientsInfo());

                        } catch (Exception e) {
                                infoArea.setText("Введите точки T-P\nДобавьте минимум 3 точки\nПодберите функцию");
                        }
                } else {
                        infoArea.setText("Введите точки T-P\nДобавьте минимум 3 точки\nПодберите функцию");
                }

                saveFunctionButton.setEnabled(false);
        }
}