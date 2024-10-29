package org.habitApp.repositories;

import org.habitApp.database.Database;
import org.habitApp.domain.entities.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователями в базе данных
 */
public class UserRepository {
    /**
     * Конструктор репозитория
     */
    public UserRepository() {
    }

    /**
     * Получение пользователя по email
     * @param email email пользователя
     * @return User
     * @throws SQLException ошибка работы с БД
     */
    public UserEntity getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToUser(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Регистрация нового пользователя
     * @param user пользователь для регистрации
     * @throws SQLException ошибка работы с БД
     */
    public void registerUser(UserEntity user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setObject(1, user.getId()); // Используем setObject для UUID
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setBoolean(5, user.isAdmin());
            statement.executeUpdate();
        }
    }

    /**
     * Обновление данных пользователя
     * @param user пользователь с новыми данными
     * @throws SQLException ошибка работы с БД
     */
    public void updateUser(UserEntity user) throws SQLException {
        String sql = "UPDATE users SET name = ?, password = ?, is_admin = ? WHERE email = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.isAdmin());
            statement.setString(4, user.getEmail());
            statement.executeUpdate();
        }
    }

    /**
     * Удаление пользователя по email
     * @param email email пользователя
     * @throws SQLException ошибка работы с БД
     */
    public void deleteUserByEmail(String email) throws SQLException {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setString(1, email);
            statement.executeUpdate();
        }
    }

    /**
     * Удаление пользователя по id
     * @param id id пользователя
     * @throws SQLException ошибка работы с БД
     */
    public void deleteUserById(UUID id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setObject(1, id); // Используем setObject для UUID
            statement.executeUpdate();
        }
    }

    /**
     * Получение всех пользователей
     * @return список пользователей
     * @throws SQLException ошибка работы с БД
     */
    public List<UserEntity> getAllUsers() throws SQLException {
        List<UserEntity> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement statement = Database.connectToDatabase().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
        }
        return users;
    }

    /**
     * Преобразование строки из ResultSet в объект User
     * @param resultSet строка результата
     * @return объект User
     * @throws SQLException ошибка работы с БД
     */
    private UserEntity mapRowToUser(ResultSet resultSet) throws SQLException {
        return new UserEntity(
                (UUID) resultSet.getObject("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("name"),
                resultSet.getBoolean("is_admin")
        );
    }
}
