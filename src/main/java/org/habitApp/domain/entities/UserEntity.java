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
    private boolean flagAdmin;

    /**
     * Конструктор User полный
     * @param id ID пользователя
     * @param email email
     * @param password пароль
     * @param name имя
     * @param flagAdmin флаг админа
     */
    public UserEntity(UUID id, String email, String password, String name, boolean flagAdmin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.flagAdmin = flagAdmin;
    }

    public UserEntity(){}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + flagAdmin +
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFlagAdmin() {
        return flagAdmin;
    }

    public void setFlagAdmin(boolean flagAdmin) {
        this.flagAdmin = flagAdmin;
    }
}
