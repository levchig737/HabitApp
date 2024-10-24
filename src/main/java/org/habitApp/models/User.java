package org.habitApp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Модель User
 */
@Getter
@Setter
@ToString
public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;

    /**
     * Конструктор User полный
     * @param id ID пользователя
     * @param email email
     * @param password пароль
     * @param name имя
     * @param isAdmin флаг админа
     */
    public User(int id, String email, String password, String name, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    /**
     * Конструктор User без ID (при создании нового пользователя)
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
