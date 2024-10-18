package habitApp.ConsoleApp;
import habitApp.services.HabitService;
import habitApp.services.HabitTrackingService;
import habitApp.services.UserService;
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
        UserService userService = new UserService();
        HabitService habitService = new HabitService();
        HabitTrackingService habitTrackingService = new HabitTrackingService();
        LoginMenu loginMenu = new LoginMenu(userService, habitService, habitTrackingService);
        loginMenu.show(new Scanner(System.in));
    }
}
