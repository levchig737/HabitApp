package org.habitApp.repositories;

import org.habitApp.annotations.Loggable;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.habitApp.domain.entities.HabitEntity.mapRowToHabit;

/**
 * Репозиторий для работы с привычками в базе данных
 */
@Repository
public class HabitRepository {
    @Autowired
    private DataSource dataSource;

    /**
     * Конструктор репозитория
     */
    @Autowired
    public HabitRepository() {
    }

    /**
     * Получение всех привычек пользователя
     * @param user пользователь
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    public List<HabitEntity> getHabitsByUser(UserEntity user) throws SQLException {
        List<HabitEntity> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits WHERE user_id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setLong(1, user.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habits.add(mapRowToHabit(resultSet));
                }
            }
        }
        return habits;
    }

    /**
     * Получение всех привычек пользователя
     * @param id id habit
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    public HabitEntity getHabitById(long id) throws SQLException {
        String sql = "SELECT * FROM habits WHERE id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToHabit(resultSet);
                }
            }
        }
        return null;
    }

    /**
     * Получение всех привычек
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    public List<HabitEntity> getAllHabits() throws SQLException {
        List<HabitEntity> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
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
    public void createHabit(HabitEntity habit) throws SQLException {
        String sql = "INSERT INTO habits (id, name, description, frequency, created_date, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setLong(1, habit.getId());
            statement.setString(2, habit.getName());
            statement.setString(3, habit.getDescription());
            statement.setString(4, habit.getFrequency());
            statement.setDate(5, Date.valueOf(habit.getCreatedDate()));
            statement.setLong(6, habit.getUserId());
            statement.executeUpdate();
        }
    }

    /**
     * Обновление привычки
     * @param habiId id habit
     * @param habit привычка
     * @throws SQLException ошибка работы с БД
     */
public void updateHabit(long habiId, HabitEntity habit) throws SQLException {
        String sql = "UPDATE habits SET name = ?, description = ?, frequency = ? WHERE id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency());
            statement.setLong(4, habiId);
            statement.executeUpdate();
        }
    }

    /**
     * Удаление привычки
     * @param habitId привычка
     * @throws SQLException ошибка работы с БД
     */
    public void deleteHabit(long habitId) throws SQLException {
        String sql = "DELETE FROM habits WHERE id = ?";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setLong(1, habitId);
            statement.executeUpdate();
        }
    }
}
