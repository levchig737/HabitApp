package org.habitApp.consoleApp;

import org.habitApp.models.User;
import org.habitApp.services.HabitService;
import org.habitApp.services.UserService;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Класс отображения меню авторизации
 */
public class LoginMenu implements Menu {
    private final UserService userService;
    private User currentUser;
    private final HabitService habitService;


    /**
     * Конструктор LoginMenu
     * @param userService UserService
     */
    public LoginMenu(UserService userService, HabitService habitService) {
        this.userService = userService;
        this.habitService = habitService;
    }

    /**
     * Выбор действия в терминале
     * @param scanner поток in
     */
    @Override
    public void show(Scanner scanner) {
        while (true) {
            System.out.println("""
                    МЕНЮ АВТОРИЗАЦИИ
                    1. Зарегистрироваться
                    2. Авторизоваться
                    3. Выйти""");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> register(scanner);
                case 2 -> login(scanner);
                case 3 -> {
                    System.out.println("Выход...");
                    System.exit(0);
                }
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Регистрация пользователя.
     * Вывод данных в консоль.
     * @param scanner поток данных in
     */
    private void register(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.next();
        System.out.print("Введите пароль: ");
        String password = scanner.next();
        System.out.print("Введите имя: ");
        String name = scanner.next();
        try {
            userService.registerUser(email, password, name);
            System.out.println("Регистрация прошла успешно.");
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Авторизация пользователя.
     * Вывод данных в консоль.
     * @param scanner поток данных in
     */
    private void login(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.next();
        System.out.print("Введите пароль: ");
        String password = scanner.next();
        try {
            currentUser = userService.loginUser(email, password);
            System.out.println("Авторизация прошла успешно.");
            new MainMenu(userService, currentUser, habitService).show(scanner);
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
