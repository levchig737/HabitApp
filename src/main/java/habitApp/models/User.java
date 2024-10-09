package habitApp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Модель User
 */
@Getter @Setter @ToString
public class User {
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;

    /**
     * Конструктор User полный
     * @param email email
     * @param password пароль
     * @param name имя
     * @param isAdmin флаг админа
     */
    public User(String email, String password, String name, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    /**
     * Конструктор User
     * isAdmin = false
     * @param email email
     * @param password пароль
     * @param name имя
     */
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = false;
    }
}


