package org.habitApp.repositories;

import org.habitApp.database.Database;
import org.habitApp.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public User getUserByEmail(String email) throws SQLException {
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
    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setInt(1, user.getId());
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
    public void updateUser(User user) throws SQLException {
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
     * Удаление пользователя по email
     * @param id id пользователя
     * @throws SQLException ошибка работы с БД
     */
    public void deleteUserById(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Получение всех пользователей
     * @return список пользователей
     * @throws SQLException ошибка работы с БД
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
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
    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("name"),
                resultSet.getBoolean("is_admin")
        );
    }
}
