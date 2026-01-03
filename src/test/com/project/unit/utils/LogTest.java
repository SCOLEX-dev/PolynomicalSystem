package test.com.project.unit.utils;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.logging.*;
import static org.junit.jupiter.api.Assertions.*;

import com.util.Log;

public class LogTest {
    private static final String TEST_LOG_FILE = "test_application.log";
    private static final String ORIGINAL_LOG_FILE = "application.log";

    @BeforeEach
    void setUp() throws Exception {
        // Удаляем существующие лог-файлы перед тестом
        deleteLogFile(TEST_LOG_FILE);
        deleteLogFile(ORIGINAL_LOG_FILE);

        // Используем рефлексию для изменения пути к лог-файлу
        System.setProperty("java.util.logging.config.file",
                createTempLogConfig(TEST_LOG_FILE));

        // Очищаем все существующие логгеры
        LogManager.getLogManager().reset();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Закрываем все обработчики
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
        }

        // Удаляем тестовые файлы
        deleteLogFile(TEST_LOG_FILE);
        deleteLogFile(ORIGINAL_LOG_FILE);
    }

    @Test
    void testGetLoggerReturnsLogger() {
        Logger logger = Log.getLogger(LogTest.class.getName());

        assertNotNull(logger);
        assertEquals(LogTest.class.getName(), logger.getName());
        assertTrue(logger.getLevel() == Level.ALL);
    }

    @Test
    void testLoggerHasFileHandler() {
        Logger logger = Log.getLogger(LogTest.class.getName());
        Handler[] handlers = logger.getHandlers();

        boolean hasFileHandler = false;
        for (Handler handler : handlers) {
            if (handler instanceof FileHandler) {
                hasFileHandler = true;
                break;
            }
        }

        assertTrue(hasFileHandler, "Логгер должен иметь FileHandler");
    }

    @Test
    void testLoggerHasConsoleHandler() {
        Logger logger = Log.getLogger(LogTest.class.getName());
        Handler[] handlers = logger.getHandlers();

        boolean hasConsoleHandler = false;
        for (Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                hasConsoleHandler = true;
                break;
            }
        }

        assertTrue(hasConsoleHandler, "Логгер должен иметь ConsoleHandler");
    }

    @Test
    void testLoggerLoggingLevels() {
        Logger logger = Log.getLogger(LogTest.class.getName());

        // Проверяем, что логгер принимает все уровни
        assertTrue(logger.isLoggable(Level.SEVERE));
        assertTrue(logger.isLoggable(Level.WARNING));
        assertTrue(logger.isLoggable(Level.INFO));
        assertTrue(logger.isLoggable(Level.CONFIG));
        assertTrue(logger.isLoggable(Level.FINE));
        assertTrue(logger.isLoggable(Level.FINER));
        assertTrue(logger.isLoggable(Level.FINEST));
    }

    @Test
    void testLoggerWritesToFile() throws Exception {
        String testClassName = "TestClass";
        String testMessage = "Test log message";

        Logger logger = Log.getLogger(testClassName);
        logger.info(testMessage);

        // Даем время на запись в файл
        Thread.sleep(100);

        // Проверяем, что сообщение записано в файл
        String logContent = readLogFile(TEST_LOG_FILE);
        assertNotNull(logContent);
        assertTrue(logContent.contains(testMessage),
                "Лог должен содержать тестовое сообщение");
        assertTrue(logContent.contains(testClassName),
                "Лог должен содержать имя класса");
    }

    @Test
    void testMultipleLoggers() {
        String className1 = "Class1";
        String className2 = "Class2";

        Logger logger1 = Log.getLogger(className1);
        Logger logger2 = Log.getLogger(className2);

        assertNotNull(logger1);
        assertNotNull(logger2);
        assertNotSame(logger1, logger2);
        assertEquals(className1, logger1.getName());
        assertEquals(className2, logger2.getName());
    }

    @Test
    void testLoggingDifferentLevels() throws Exception {
        Logger logger = Log.getLogger("TestLevels");

        // Логируем сообщения разных уровней
        logger.severe("SEVERE message");
        logger.warning("WARNING message");
        logger.info("INFO message");
        logger.config("CONFIG message");
        logger.fine("FINE message");
        logger.finer("FINER message");
        logger.finest("FINEST message");

        Thread.sleep(100);

        String logContent = readLogFile(TEST_LOG_FILE);

        // Проверяем, что разные уровни записаны
        assertTrue(logContent.contains("SEVERE"));
        assertTrue(logContent.contains("WARNING"));
        assertTrue(logContent.contains("INFO"));
        // Менее важные уровни могут не записываться в файл в зависимости от конфигурации
    }

    @Test
    void testLoggerWithException() throws Exception {
        Logger logger = Log.getLogger("TestException");
        Exception testException = new IOException("Test IO Exception");

        logger.log(Level.SEVERE, "Exception occurred", testException);

        Thread.sleep(100);

        String logContent = readLogFile(TEST_LOG_FILE);

        assertTrue(logContent.contains("Exception occurred"));
        assertTrue(logContent.contains("IOException"));
        assertTrue(logContent.contains("Test IO Exception"));
    }

    @Test
    void testLoggerPerformance() {
        Logger logger = Log.getLogger("TestPerformance");

        long startTime = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            logger.info("Performance test message " + i);
        }

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        System.out.println("Время логирования 1000 сообщений: " +
                String.format("%.2f", durationMs) + " мс");

        assertTrue(durationMs < 1000,
                "Логирование 1000 сообщений должно занимать менее 1 секунды");
    }

    @Test
    void testLogFileAppendMode() throws Exception {
        Logger logger = Log.getLogger("TestAppend");

        // Первое сообщение
        logger.info("First message");
        Thread.sleep(100);

        // Второе сообщение
        logger.info("Second message");
        Thread.sleep(100);

        String logContent = readLogFile(TEST_LOG_FILE);

        // Проверяем, что оба сообщения в файле
        assertTrue(logContent.contains("First message"));
        assertTrue(logContent.contains("Second message"));

        // Проверяем порядок (первое сообщение должно быть раньше)
        int firstIndex = logContent.indexOf("First message");
        int secondIndex = logContent.indexOf("Second message");
        assertTrue(firstIndex < secondIndex,
                "Сообщения должны быть в правильном порядке");
    }

    @Test
    void testLoggerThreadSafety() throws InterruptedException {
        Logger logger = Log.getLogger("TestThreadSafety");

        int threadCount = 10;
        int messagesPerThread = 100;
        Thread[] threads = new Thread[threadCount];

        // Создаем и запускаем потоки
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < messagesPerThread; j++) {
                    logger.info("Thread " + threadId + " message " + j);
                }
            });
            threads[i].start();
        }

        // Ждем завершения всех потоков
        for (Thread thread : threads) {
            thread.join();
        }

        // Даем время на запись в файл
        Thread.sleep(500);

        // Проверяем, что файл создан и не пустой
        File logFile = new File(TEST_LOG_FILE);
        assertTrue(logFile.exists());
        assertTrue(logFile.length() > 0);
    }

    @Test
    void testGetLoggerWithNull() {
        // Проверяем, что метод не падает с null
        Logger logger = Log.getLogger(null);
        assertNotNull(logger);
        assertEquals("", logger.getName()); // null становится пустой строкой
    }

    @Test
    void testGetLoggerWithEmptyString() {
        Logger logger = Log.getLogger("");
        assertNotNull(logger);
        assertEquals("", logger.getName());
    }

    @Test
    void testLogFormat() throws Exception {
        Logger logger = Log.getLogger("TestFormat");

        // Логируем сообщение с параметрами
        String user = "testUser";
        int action = 42;
        logger.log(Level.INFO, "User {0} performed action {1}",
                new Object[]{user, action});

        Thread.sleep(100);

        String logContent = readLogFile(TEST_LOG_FILE);

        // Проверяем формат сообщения
        assertTrue(logContent.contains("User testUser performed action 42"));

        // Проверяем стандартный формат (дата, уровень, класс, сообщение)
        assertTrue(logContent.matches(".*\\d{4}-\\d{2}-\\d{2}.*")); // Дата
        assertTrue(logContent.contains("INFO")); // Уровень
        assertTrue(logContent.contains("TestFormat")); // Класс
    }

    // Вспомогательные методы

    private void deleteLogFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    private String readLogFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private String createTempLogConfig(String logFileName) throws IOException {
        File tempConfig = File.createTempFile("logging", ".properties");
        tempConfig.deleteOnExit();

        String configContent = String.format(
                "handlers = java.util.logging.FileHandler, java.util.logging.ConsoleHandler\n" +
                        ".level = ALL\n" +
                        "java.util.logging.FileHandler.level = ALL\n" +
                        "java.util.logging.FileHandler.pattern = %s\n" +
                        "java.util.logging.FileHandler.limit = 1000000\n" +
                        "java.util.logging.FileHandler.count = 1\n" +
                        "java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter\n" +
                        "java.util.logging.ConsoleHandler.level = INFO\n" +
                        "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter\n" +
                        "java.util.logging.SimpleFormatter.format = %%1$tY-%%1$tm-%%1$td %%1$tH:%%1$tM:%%1$tS %%4$s %%2$s: %%5$s%%6$s%%n",
                logFileName
        );

        try (FileWriter writer = new FileWriter(tempConfig)) {
            writer.write(configContent);
        }

        return tempConfig.getAbsolutePath();
    }

    @Test
    void testMemoryUsage() {
        Logger logger = Log.getLogger("TestMemory");

        // Проверяем, что создание многих логгеров не вызывает утечек памяти
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Создаем много логгеров
        for (int i = 0; i < 1000; i++) {
            Log.getLogger("TestMemory" + i);
        }

        runtime.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        System.out.println("Увеличение памяти при создании 1000 логгеров: " +
                memoryIncrease + " байт");

        assertTrue(memoryIncrease < 10_000_000,
                "Создание логгеров не должно потреблять слишком много памяти");
    }
}