package habitApp;

import habitApp.models.User;
import habitApp.services.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Интерактивное консольное приложение HabitApp
 */
public class ConsoleApp {
    private static final UserService userService = new UserService();
    private static User currentUser;

    /**
     * Запуск приложения.
     * Меню авторизации
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        showLoginMenu(scanner);
    }

    public static void showLoginMenu(Scanner scanner) {
        while (true) {
            System.out.println("1. Зарегистироваться\n2. Авторизоваться\n3. Выйти");
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
    private static void register(Scanner scanner) {
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
    private static void login(Scanner scanner) {
        System.out.print("Введите email: ");
        String email = scanner.next();
        System.out.print("Введите пароль: ");
        String password = scanner.next();
        try {
            currentUser = userService.loginUser(email, password);
            System.out.println("Авторизация прошла успешно.");
            showMainMenu(scanner);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление currentUser
     * @param scanner поток in
     */
    private static void updateCurrentUser(Scanner scanner){
        System.out.print("Введите новое имя: ");
        String name = scanner.next();
        System.out.print("Введите новый пароль: ");
        String password = scanner.next();

        try {
            User user = userService.updateCurrentUserProfile(name, password);
            System.out.println(user.toString());
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Выход из аккаунта пользователя.
     * Переход в
     * @param scanner поток данных in
     */
    private static void unLogin(Scanner scanner) {
        try {
            userService.unLoginUser();
            currentUser = null;

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Удаление из аккаунта пользователя.
     * @param scanner поток данных in
     */
    private static void deleteCurrentUser(Scanner scanner) {
        try {
            userService.deleteCurrentUser();
            currentUser = null;

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Главное меню приложения
     * @param scanner поток данных in
     */
    private static void showMainMenu(Scanner scanner) {
        while (true) {
            System.out.println("1. \n2. Обновить профиль \n3 Удалить аккаунт\n4. Меню админа \n5. Выйти из аккаунта");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {return;}
                case 2 -> {updateCurrentUser(scanner);}
                case 3 -> {deleteCurrentUser(scanner);}
                case 4 -> {showAdminMenu(scanner);}
                case 5 -> {unLogin(scanner);}
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Получение user по email
     * @param scanner поток in
     */
    private static void getUserByEmail(Scanner scanner){
        System.out.print("Введите email: ");
        String email = scanner.next();
        try {
            User user = userService.getUser(email);
            System.out.println(user.toString());
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Получение всех users
     * @param scanner поток in
     */
    private static void getUsers(Scanner scanner){
        try {
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                System.out.println(user.toString());
            }
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление user по email
     * @param scanner поток in
     */
    private static void updateUserByEmail(Scanner scanner){
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
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление user по email
     * @param scanner поток in
     */
    private static void deleteUser(Scanner scanner){
        System.out.print("Введите email: ");
        String email = scanner.next();

        try {
            userService.deleteUser(email);
            System.out.println("Пользователь " + email + " удален");
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Меню админа
     * @param scanner поток in
     */
    private static void showAdminMenu(Scanner scanner) {
        while (true) {
            System.out.println("1. Получить пользователя по email \n2. Получить всех пользователей \n3. Обновить пользователя по email \n 4. Удалить пользователя по email \n5. Вернутся в главное меню");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {getUserByEmail(scanner);}
                case 2 -> {getUsers(scanner);}
                case 3 -> {updateUserByEmail(scanner);}
                case 4 -> {deleteUser(scanner);}
                case 5 -> {showMainMenu(scanner);}
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }
}

