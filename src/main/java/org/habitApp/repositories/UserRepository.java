package org.habitApp.repositories;

import org.habitApp.domain.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с данными пользователей.
 * Содержит методы для выполнения операций с базой данных, связанных с пользователями.
 */
@Repository
public class UserRepository {

    @Autowired
    private DataSource dataSource; // Источник данных для подключения к базе данных

    @Autowired
    public UserRepository() {
    }

    /**
     * Получение пользователя по email.
     *
     * @param email Email пользователя
     * @return Пользователь с указанным email или null, если не найден
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public UserEntity getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
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
     * Получение пользователя по ID.
     *
     * @param id ID пользователя
     * @return Пользователь с указанным ID или null, если не найден
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public UserEntity getUserById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToUser(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Регистрация нового пользователя в базе данных.
     *
     * @param user Пользователь, который должен быть зарегистрирован
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public void registerUser(UserEntity user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setBoolean(5, user.isFlagAdmin());
            statement.executeUpdate();
        }
    }

    /**
     * Обновление информации о пользователе.
     *
     * @param user Пользователь с обновленной информацией
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public void updateUser(UserEntity user) throws SQLException {
        String sql = "UPDATE users SET name = ?, password = ?, is_admin = ? WHERE email = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.isFlagAdmin());
            statement.setString(4, user.getEmail());
            statement.executeUpdate();
        }
    }

    /**
     * Удаление пользователя по email.
     *
     * @param email Email пользователя, которого нужно удалить
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public void deleteUserByEmail(String email) throws SQLException {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setString(1, email);
            statement.executeUpdate();
        }
    }

    /**
     * Удаление пользователя по ID.
     *
     * @param id ID пользователя, которого нужно удалить
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public void deleteUserById(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Получение списка всех пользователей.
     *
     * @return Список всех пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public List<UserEntity> getAllUsers() throws SQLException {
        List<UserEntity> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement statement = dataSource.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
        }
        return users;
    }

    /**
     * Преобразование строки результата запроса в объект UserEntity.
     *
     * @param resultSet Результат запроса
     * @return Объект UserEntity, соответствующий строке результата
     * @throws SQLException В случае ошибок при работе с результатом запроса
     */
    private UserEntity mapRowToUser(ResultSet resultSet) throws SQLException {
        return new UserEntity(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("name"),
                resultSet.getBoolean("is_admin")
        );
    }
}
