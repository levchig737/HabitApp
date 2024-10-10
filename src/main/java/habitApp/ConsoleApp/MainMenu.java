package habitApp.ConsoleApp;

import habitApp.models.User;
import habitApp.services.HabitService;
import habitApp.services.HabitTrackingService;
import habitApp.services.UserService;

import java.util.Scanner;

/**
 * Класс отображения главного
 */
public class MainMenu implements Menu {
    private final UserService userService;
    private User currentUser;
    private final HabitService habitService;
    private final HabitTrackingService habitTrackingService;

    /**
     * Конструктор MainMenu
     * @param userService UserService
     * @param currentUser текущий пользователь
     */
    public MainMenu(UserService userService, User currentUser, HabitService habitService,
                    HabitTrackingService habitTrackingService) {
        this.userService = userService;
        this.currentUser = currentUser;
        this.habitService = habitService;
        this.habitTrackingService = habitTrackingService;
    }

    /**
     * Выбор действия в терминале
     * @param scanner поток in
     */
    @Override
    public void show(Scanner scanner) {
        while (true) {
            System.out.println("""
                    ГЛАВНОЕ МЕНЮ
                    1. Меню привычек
                    2. Изменить профиль
                    3. Меню админа
                    4. Выйти из аккаунта""");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> new HabitMenu(habitService, habitTrackingService, currentUser).show(scanner);
                case 2 -> new ProfileMenu(userService, currentUser).show(scanner);
                case 3 -> new AdminMenu(userService).show(scanner);
                case 4 -> {unLogin(); return;}
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Выход из аккаунта пользователя.
     * Переход в LoginMenu
     */
    private void unLogin() {
        System.out.println("Выход из аккаунта...");
        userService.unLoginUser();
        currentUser = null;
//        new LoginMenu(userService).show(new Scanner(System.in));
        return;
    }
}

