package org.habitApp.repositories;

import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.habitApp.domain.entities.HabitEntity.mapRowToHabit;
import static org.habitApp.repositories.constants.HabitSqlQueries.*;

/**
 * Репозиторий для работы с привычками в базе данных
 */
@Repository
public class HabitRepository {

    private final DataSource dataSource;

    /**
     * Конструктор репозитория
     */
    public HabitRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Получение всех привычек пользователя
     * @param user пользователь
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    public List<HabitEntity> getHabitsByUser(UserEntity user) throws SQLException {
        List<HabitEntity> habits = new ArrayList<>();
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_HABITS_BY_USER_ID)) {
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
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_HABIT_BY_ID)) {
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
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_ALL_HABITS)) {
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
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(CREATE_HABIT)) {
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
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(UPDATE_HABIT)) {
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
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(DELETE_HABIT)) {
            statement.setLong(1, habitId);
            statement.executeUpdate();
        }
    }
}
