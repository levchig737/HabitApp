package habitApp.ConsoleApp;
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
        LoginMenu loginMenu = new LoginMenu(userService);
        loginMenu.show(new Scanner(System.in));
    }
}
