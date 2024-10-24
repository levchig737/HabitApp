package org.habitApp.repositories;

import org.habitApp.database.Database;
import org.habitApp.models.Habit;
import org.habitApp.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.habitApp.models.Habit.mapRowToHabit;

/**
 * Репозиторий для работы с привычками в базе данных
 */
public class HabitRepository {
    /**
     * Конструктор репозитория
     */
    public HabitRepository() {
    }

    /**
     * Получение всех привычек пользователя
     * @param user пользователь
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    public List<Habit> getHabitsByUser(User user) throws SQLException {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits WHERE user_id = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setInt(1, user.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habits.add(mapRowToHabit(resultSet));
                }
            }
        }
        return habits;
    }
    /**
     * Получение всех привычек
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    public List<Habit> getAllHabits() throws SQLException {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habits.add(mapRowToHabit(resultSet));
                }
            }
        }
        return habits;
    }

    /**
     * Создание новой привычки
     * @param habit привычка
     * @throws SQLException ошибка работы с БД
     */
    public void createHabit(Habit habit) throws SQLException {
        String sql = "INSERT INTO habits (name, description, frequency, created_date, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency());
            statement.setDate(4, Date.valueOf(habit.getCreatedDate()));
            statement.setInt(5, habit.getUserId());
            statement.executeUpdate();
        }
    }

    /**
     * Обновление привычки
     * @param habit привычка
     * @throws SQLException ошибка работы с БД
     */
    public void updateHabit(Habit habit) throws SQLException {
        String sql = "UPDATE habits SET name = ?, description = ?, frequency = ? WHERE id = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency());
            statement.setInt(4, habit.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Удаление привычки
     * @param habit привычка
     * @throws SQLException ошибка работы с БД
     */
    public void deleteHabit(Habit habit) throws SQLException {
        String sql = "DELETE FROM habits WHERE id = ?";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setInt(1, habit.getId());
            statement.executeUpdate();
        }
    }
}
