package org.habitApp.services;

import org.habitApp.domain.entities.UserEntity;
import org.habitApp.repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * User сервис
 * Сервис бизнес логики над User
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Конструктор с инициализацией репозитория пользователей
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Регистрация user
     *
     * @param email    email
     * @param password пароль
     * @param name     имя
     * @throws SQLException ошибка работы с БД
     */
    public void registerUser(String email, String password, String name) throws SQLException {
        if (userRepository.getUserByEmail(email) != null) {
            throw new IllegalArgumentException("User already exists.");
        }
        UserEntity user = UserEntity.CreateUser(email, password, name);
        userRepository.registerUser(user);
    }

    /**
     * Авторизация user
     *
     * @param email    email
     * @param password пароль
     * @return User
     * @throws SQLException ошибка работы с БД
     */
    public UserEntity loginUser(String email, String password) throws SQLException {
        UserEntity user = userRepository.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        throw new IllegalArgumentException("Invalid email or password.");
    }

    /**
     * Выход из аккаунта
     */
    public void unLoginUser() {
    }

    /**
     * Обновление currentUser
     *
     * @param newName     новое имя
     * @param newPassword новый пароль
     * @throws SQLException ошибка работы с БД
     */
    public void updateCurrentUserProfile(String newName, String newPassword, UserEntity currentUser) throws SQLException {
        if (currentUser != null) {
            currentUser.setName(newName);
            currentUser.setPassword(newPassword);
            userRepository.updateUser(currentUser);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    /**
     * Удаление текущего пользователя
     *
     * @throws SQLException ошибка работы с БД
     */
    public void deleteCurrentUser(UserEntity currentUser) throws SQLException {
        if (currentUser != null) {
            userRepository.deleteUserById(currentUser.getId());
            currentUser = null;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    /**
     * Получение user по email
     * @param email email
     * @return User
     * @throws SQLException ошибка работы с БД
     */
    public UserEntity getUser(String email, UserEntity currentUser) throws SQLException, IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        return userRepository.getUserByEmail(email);
    }

    /**
     * Получение списка всех user
     * @return список users
     * @throws SQLException ошибка работы с БД
     */
    public List<UserEntity> getAllUsers(UserEntity currentUser) throws SQLException, IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        return userRepository.getAllUsers();
    }

    /**
     * Обновление user
     * Обновляет все данные пользователя.
     * @param email       email
     * @param newName     новое имя
     * @param newPassword новый пароль
     * @throws SQLException ошибка работы с БД
     */
    public UserEntity updateUserProfile(String email, String newName, String newPassword, boolean isAdmin, UserEntity currentUser) throws SQLException, IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        UserEntity user = userRepository.getUserByEmail(email);
        if (user != null) {
            user.setName(newName);
            user.setPassword(newPassword);
            user.setAdmin(isAdmin);
            userRepository.updateUser(user);
            return user;
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    /**
     * Удаление user
     * @param id id user
     * @throws SQLException ошибка работы с БД
     */
    public void deleteUser(UUID id, UserEntity currentUser) throws SQLException, IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        userRepository.deleteUserById(id);
    }
}
