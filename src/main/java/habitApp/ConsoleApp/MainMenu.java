package habitApp.ConsoleApp;

import habitApp.models.User;
import habitApp.services.UserService;

import java.util.Scanner;

/**
 * Класс отображения главного
 */
public class MainMenu implements Menu {
    private final UserService userService;
    private final User currentUser;

    /**
     * Конструктор MainMenu
     * @param userService UserService
     */
    public MainMenu(UserService userService, User currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    /**
     * Выбор действия в терминале
     * @param scanner поток in
     */
    @Override
    public void show(Scanner scanner) {
        while (true) {
            System.out.println("1. \n2. Изменить профиль\n3. Меню админа\n4. Выйти из аккаунта");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {return;}
                case 2 -> new ProfileMenu(userService, currentUser).show(scanner);
                case 3 -> new AdminMenu(userService).show(scanner);
                case 4 -> unLogin();
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Выход из аккаунта пользователя.
     * Переход в
     */
    private void unLogin() {
        System.out.println("Выход из аккаунта...");
        // Вернуться в меню авторизации
        new LoginMenu(userService).show(new Scanner(System.in));
    }
}

