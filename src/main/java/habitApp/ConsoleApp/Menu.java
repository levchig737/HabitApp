package habitApp.ConsoleApp;

import habitApp.models.User;
import habitApp.services.UserService;

import java.util.Scanner;

/**
 * Интерфейс меню
 */
public interface Menu {
    /**
     * Ото
     * @param scanner
     */
    public void show(Scanner scanner);
}
