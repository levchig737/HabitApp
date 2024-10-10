package habitApp.ConsoleApp;

import habitApp.models.User;
import habitApp.services.UserService;

import java.util.Scanner;

public class ProfileMenu implements Menu{
    private final UserService userService;
    private final User currentUser;

    /**
     * Конструктор MainMenu
     * @param userService UserService
     */
    public ProfileMenu(UserService userService, User currentUser) {
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
            System.out.println("""
                    МЕНЮ ПРОФИЛЯ
                    1. Обновить данные
                    2. Удалить аккаунт
                    3. Назад""");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> updateCurrentUser(scanner);
                case 2 -> deleteCurrentUser(scanner);
                case 3 -> {return;}
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Обновление currentUser
     * @param scanner поток in
     */
    private void updateCurrentUser(Scanner scanner) {
        System.out.print("Введите новое имя: ");
        String name = scanner.next();
        System.out.print("Введите новый пароль: ");
        String password = scanner.next();

        try {
            User user = userService.updateCurrentUserProfile(name, password);
            System.out.println(user.toString());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Удаление из аккаунта пользователя.
     * @param scanner поток данных in
     */
    private void deleteCurrentUser(Scanner scanner) {
        try {
            userService.deleteCurrentUser();
            System.out.println("Ваш аккаунт был удален.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
