package org.habitApp.repositories.constants;

/**
 * Список констант запросов к таблице users
 */
public class UserSqlQueries {
    public static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    public static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String REGISTER_USER = "INSERT INTO users (id, name, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_USER = "UPDATE users SET name = ?, password = ?, is_admin = ? WHERE email = ?";
    public static final String DELETE_USER_BY_EMAIL = "DELETE FROM users WHERE email = ?";
    public static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    public static final String GET_ALL_USERS = "SELECT * FROM users";
}
