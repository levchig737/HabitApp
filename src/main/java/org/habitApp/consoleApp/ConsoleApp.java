package org.habitApp.consoleApp;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.HabitService;
import org.habitApp.services.UserService;

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
