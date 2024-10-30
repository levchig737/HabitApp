package org.habitApp.services;

import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * User сервис
 * Сервис бизнес логики над User
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    /**
     * Регистрация user
     *
     * @param email    email
     * @param password пароль
     * @param name     имя
     */
    public void registerUser(String email, String password, String name) throws SQLException {
        if (userRepository.getUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("User already exists.");
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
     */
    public UserEntity loginUser(String email, String password) throws SQLException {
        UserEntity user = userRepository.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        throw new InvalidCredentialsException("Invalid email or password.");
    }

    /**
     * Выход из аккаунта
     */
    public void unLoginUser() {
        // Логика выхода
    }

    /**
     * Обновление currentUser
     *
     * @param newName     новое имя
     * @param newPassword новый пароль
     * @throws UserNotFoundException если пользователь не найден
     */
    public void updateCurrentUserProfile(String newName, String newPassword, UserEntity currentUser) throws SQLException {
        if (currentUser != null) {
            currentUser.setName(newName);
            currentUser.setPassword(newPassword);
            userRepository.updateUser(currentUser);
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    /**
     * Удаление текущего пользователя
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    public void deleteCurrentUser(UserEntity currentUser) throws SQLException {
        if (currentUser != null) {
            userRepository.deleteUserById(currentUser.getId());
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    /**
     * Получение user по email
     * @param email email
     * @return User
     * @throws UnauthorizedAccessException если пользователь не администратор
     */
    public UserEntity getUser(String email, UserEntity currentUser) throws SQLException {
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        return userRepository.getUserByEmail(email);
    }

    /**
     * Получение списка всех user
     * @return список users
     * @throws UnauthorizedAccessException если пользователь не администратор
     */
    public List<UserEntity> getAllUsers(UserEntity currentUser) throws SQLException {
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        return userRepository.getAllUsers();
    }

    /**
     * Обновление user
     * @param email       email
     * @param newName     новое имя
     * @param newPassword новый пароль
     * @throws UnauthorizedAccessException если пользователь не администратор
     * @throws UserNotFoundException если пользователь не найден
     */
    public UserEntity updateUserProfile(String email, String newName, String newPassword, boolean isAdmin, UserEntity currentUser) throws SQLException {
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        UserEntity user = userRepository.getUserByEmail(email);
        if (user != null) {
            user.setName(newName);
            user.setPassword(newPassword);
            user.setAdmin(isAdmin);
            userRepository.updateUser(user);
            return user;
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    /**
     * Удаление user
     * @param id id user
     * @throws UnauthorizedAccessException если пользователь не администратор
     */
    public void deleteUser(UUID id, UserEntity currentUser) throws SQLException {
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        userRepository.deleteUserById(id);
    }
}
