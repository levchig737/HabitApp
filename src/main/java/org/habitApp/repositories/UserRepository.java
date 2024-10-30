package org.habitApp.repositories;

import org.habitApp.domain.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {
    @Autowired
    private DataSource dataSource;

    @Autowired
    public UserRepository() {
    }

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

    public void registerUser(UserEntity user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setBoolean(5, user.isAdmin());
            statement.executeUpdate();
        }
    }

    public void updateUser(UserEntity user) throws SQLException {
        String sql = "UPDATE users SET name = ?, password = ?, is_admin = ? WHERE email = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.isAdmin());
            statement.setString(4, user.getEmail());
            statement.executeUpdate();
        }
    }

    public void deleteUserByEmail(String email) throws SQLException {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setString(1, email);
            statement.executeUpdate();
        }
    }

    public void deleteUserById(UUID id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        }
    }

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
