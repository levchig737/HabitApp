package org.habitApp.repositories.constants;

/**
 * Список констант запросов к таблице users
 */
public class UserSqlQueries {
    public static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    public static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String REGISTER_USER = "INSERT INTO users (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_USER = "UPDATE users SET username = ?, password = ?, role = ? WHERE email = ?";
    public static final String DELETE_USER_BY_EMAIL = "DELETE FROM users WHERE email = ?";
    public static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    public static final String GET_ALL_USERS = "SELECT * FROM users";
}
