package habitApp.ConsoleApp;

import habitApp.models.User;
import habitApp.services.UserService;

import java.util.Scanner;

/**
 * Класс отображения меню авторизации
 */
public class LoginMenu implements Menu {
    private final UserService userService;
    private User currentUser;

    /**
     * Конструктор LoginMenu
     * @param userService UserService
     */
    public LoginMenu(UserService userService) {
        this.userService = userService;
    }

    /**
     * Выбор действия в терминале
     * @param scanner поток in
     */
    @Override
    public void show(Scanner scanner) {
        while (true) {
            System.out.println("1. Зарегистрироваться\n2. Авторизоваться\n3. Выйти");
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
        } catch (IllegalArgumentException e) {
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
            new MainMenu(userService, currentUser).show(scanner);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
