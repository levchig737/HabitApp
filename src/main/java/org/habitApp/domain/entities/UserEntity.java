package org.habitApp.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Модель User
 */
@Getter
@Setter
public class UserEntity {
    private UUID id;
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
    public UserEntity(UUID id, String email, String password, String name, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }

    /**
     * Конструктор User без ID (при создании нового пользователя)
     * @param email email
     * @param password пароль
     * @param name имя
     */
    public static UserEntity CreateUser(String email, String password, String name) {
        return new UserEntity(UUID.randomUUID(), email, password, name, false);
    }
}
