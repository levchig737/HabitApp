package org.habitApp.domain.entities;

import java.util.UUID;

/**
 * Модель User
 */
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
