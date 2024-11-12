package org.habitApp.repositories.constants;

/**
 * Список констант запросов к таблице habits
 */
public class HabitSqlQueries {
    public static final String GET_HABITS_BY_USER_ID = "SELECT * FROM habits WHERE user_id = ?";
    public static final String GET_HABIT_BY_ID = "SELECT * FROM habits WHERE id = ?";
    public static final String GET_ALL_HABITS = "SELECT * FROM habits";
    public static final String CREATE_HABIT = "INSERT INTO habits (id, name, description, frequency, created_date, user_id) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_HABIT = "UPDATE habits SET name = ?, description = ?, frequency = ? WHERE id = ?";
    public static final String DELETE_HABIT = "DELETE FROM habits WHERE id = ?";
}