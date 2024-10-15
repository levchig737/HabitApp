package habitApp;

import habitApp.ConsoleApp.ConsoleApp;
import habitApp.database.Database;

/**
 * Main класс приложения
 * Запускает конслольное приложение
 */
public class Main {
    public static void main(String[] args) {
        Database.ConnectToDB();
        ConsoleApp consoleApp = new ConsoleApp();
        consoleApp.run();
    }

}