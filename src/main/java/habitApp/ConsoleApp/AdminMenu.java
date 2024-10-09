package habitApp.ConsoleApp;

import habitApp.models.User;
import habitApp.services.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Класс отображения меню админа
 */
public class AdminMenu implements Menu {
    private final UserService userService;

    /**
     * Конструктор AdminMenu
     * @param userService UserService
     */
    public AdminMenu(UserService userService) {
        this.userService = userService;
    }

    /**
     * Выбор действия в терминале
     * @param scanner поток in
     */
    @Override
    public void show(Scanner scanner) {
        while (true) {
            System.out.println("1. Получить пользователя по email\n2. Получить всех пользователей\n3. Обновить пользователя по email\n4. Удалить пользователя по email\n5. Вернуться в главное меню");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> getUserByEmail(scanner);
                case 2 -> getAllUsers();
                case 3 -> updateUserByEmail(scanner);
                case 4 -> deleteUser(scanner);
                case 5 -> {return;} // Возвращаемся в главное меню
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Получение user по email
     * @param scanner поток in
     */
    private void getUserByEmail(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.next();
        try {
            User user = userService.getUser(email);
            System.out.println(user.toString());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Получение всех users
     */
    private void getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                System.out.println(user.toString());
            }
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление user по email
     * @param scanner поток in
     */
    private void updateUserByEmail(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.next();
        System.out.print("Введите новое имя: ");
        String name = scanner.next();
        System.out.print("Введите новый пароль: ");
        String password = scanner.next();
        System.out.print("Введите новый флаг админа (true/false): ");
        Boolean isAdmin = Boolean.valueOf(scanner.next());

        try {
            User user = userService.updateUserProfile(email, name, password, isAdmin);
            System.out.println(user.toString());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление user по email
     * @param scanner поток in
     */
    private void deleteUser(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.next();

        try {
            userService.deleteUser(email);
            System.out.println("Пользователь " + email + " удален");
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }
}
