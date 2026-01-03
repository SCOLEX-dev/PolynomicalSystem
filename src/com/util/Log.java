package com.util;

import java.io.*;
import java.util.logging.*;

public class Log {
    private static final String LOG_FILE = "application.log";
    private static FileHandler fileHandler;

    static {
        try {
            fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Ошибка при создании файлового обработчика: " + e.getMessage());
        }
    }

    public static Logger getLogger(String className) {
        Logger logger = Logger.getLogger(className);

        try {
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);

            if (fileHandler != null) {
                logger.addHandler(fileHandler);
            }

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);
            logger.addHandler(consoleHandler);

        } catch (Exception e) {
            System.err.println("Ошибка при создании логгера: " + e.getMessage());
        }

        return logger;
    }
}