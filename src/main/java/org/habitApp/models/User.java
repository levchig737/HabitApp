package org.habitApp.models;

import java.util.Random;

/**
 * Модель User
 */
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
    public User(String email, String password, String name) {
        Random random = new Random();
        this.id = random.nextInt();
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = false;
    }

    public void setId(int id) {
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

    public int getId() {
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
