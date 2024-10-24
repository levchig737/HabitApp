package org.habitApp;

import org.habitApp.consoleApp.ConsoleApp;
import org.habitApp.database.Database;

/**
 * Main класс приложения
 * Запускает конслольное приложение
 */
public class Main {
    public static void main(String[] args) {
        Database.runDB();
        ConsoleApp consoleApp = new ConsoleApp();
        consoleApp.run();
    }

}