package habitApp.ConsoleApp;
import habitApp.database.Database;
import habitApp.repositories.HabitComletionHistoryRepository;
import habitApp.repositories.HabitRepository;
import habitApp.repositories.UserRepository;
import habitApp.services.HabitService;
import habitApp.services.UserService;

import java.sql.Connection;
import java.util.Scanner;


/**
 * Интерактивное консольное приложение
 */
public class ConsoleApp {
    /**
     * Запуск приложения.
     * Меню авторизации
     */
    public void run() {
        // Репозитории
        UserRepository userRepository = new UserRepository();
        HabitRepository habitRepository = new HabitRepository();
        HabitComletionHistoryRepository habitComletionHistoryRepository = new HabitComletionHistoryRepository();

        // Сервисы
        UserService userService = new UserService(userRepository);
        HabitService habitService = new HabitService(habitRepository, habitComletionHistoryRepository);
        LoginMenu loginMenu = new LoginMenu(userService, habitService);
        loginMenu.show(new Scanner(System.in));
    }
}
