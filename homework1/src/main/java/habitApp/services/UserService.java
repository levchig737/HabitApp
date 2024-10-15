package habitApp.services;

import habitApp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User сервис
 * Сервис бизнес логики над User
 */
public class UserService {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    /**
     * Конструктор добавление админа в "бд"
     */
    public UserService() {
        users.put("root", new User("root", "root", "root", true));
    }

    /**
     * Регистрация user
     *
     * @param email    email
     * @param password пароль
     * @param name     имя
     */
    public void registerUser(String email, String password, String name) {
        if (users.containsKey(email)) {
            throw new IllegalArgumentException("User already exists.");
        }
        User user = new User(email, password, name);
        users.put(email, user);
    }

    /**
     * Авторизация user
     *
     * @param email    email
     * @param password пароль
     * @return User
     */
    public User loginUser(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return user;
        }
        throw new IllegalArgumentException("Invalid email or password.");
    }

    /**
     * Выход из аккаунта
     */
    public void unLoginUser() {
        currentUser = null;
    }

    /**
     * Обновление currentUser
     *
     * @param newName     новое имя
     * @param newPassword новый пароль
     */
    public User updateCurrentUserProfile(String newName, String newPassword) {
        if (currentUser != null) {
            User user = users.get(currentUser.getEmail());
            user.setName(newName);
            user.setPassword(newPassword);
            return user;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    /**
     * Удаление текущего пользователя
     */
    public void deleteCurrentUser() {
        if (currentUser != null) {
            users.remove(currentUser.getEmail());
            currentUser = null;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    /**
     * Получение user по email
     * @param email email
     * @return User
     * @throws IllegalAccessException Только админ
     */
    public User getUser(String email) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }

        return users.get(email);
    }

    /**
     * Получение списка всех user
     * @return список users
     * @throws IllegalAccessException Доступ только у админа
     */
    public List<User> getAllUsers() throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }

        return new ArrayList<>(users.values());
    }

    /**
     * Обновление user
     * Обновляет все данные пользователя.
     * @param email       email
     * @param newName     новое имя
     * @param newPassword новый пароль
     * @throws IllegalAccessException Доступ только у админа
     */
    public User updateUserProfile(String email, String newName, String newPassword, boolean isAdmin) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        User user = users.get(email);
        if (user != null) {
            user.setName(newName);
            user.setPassword(newPassword);
            user.setAdmin(isAdmin);
            return user;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    /**
     * Удаление user
     * @param email email
     * @throws IllegalAccessException Доступ только у админа
     */
    public void deleteUser(String email) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        if (!users.containsKey(email)) {
            throw new IllegalArgumentException("User not found.");
        }
        users.remove(email);
    }
}
