package org.habitApp.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.repositories.HabitRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.habitApp.repositories.constants.HabitSqlQueries.*;

/**
 * Репозиторий для работы с привычками в базе данных
 */
@Repository
@RequiredArgsConstructor
public class HabitRepositoryImpl implements HabitRepository {

    private final DataSource dataSource;

    /**
     * Получение всех привычек пользователя
     * @param user пользователь
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public List<HabitEntity> getHabitsByUser(UserEntity user) throws SQLException {
        List<HabitEntity> habits = new ArrayList<>();
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_HABITS_BY_USER_ID)) {
            statement.setLong(1, user.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habits.add(mapRowToEntity(resultSet));
                }
            }
        }
        return habits;
    }

    /**
     * Получение привычки по ID
     * @param id id habit
     * @return Optional с привычкой, если найдена, или пустой Optional, если нет
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public Optional<HabitEntity> findById(Long id) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_HABIT_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToEntity(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Получение всех привычек
     * @return список привычек
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public List<HabitEntity> findAll() throws SQLException {
        List<HabitEntity> habits = new ArrayList<>();
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_ALL_HABITS)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habits.add(mapRowToEntity(resultSet));
                }
            }
        }
        return habits;
    }

    /**
     * Создание новой привычки
     * @param habit привычка
     * @return созданная привычка с установленным идентификатором
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public HabitEntity create(HabitEntity habit) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(CREATE_HABIT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency());
            statement.setDate(4, Date.valueOf(habit.getCreatedDate()));
            statement.setLong(5, habit.getUserId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    habit.setId(generatedKeys.getLong(1));
                }
            }
        }
        return habit;
    }

    /**
     * Обновление привычки
     * @param habit обновленная привычка
     * @return true, если обновление прошло успешно, иначе false
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public boolean update(HabitEntity habit) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(UPDATE_HABIT)) {
            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency());
            statement.setLong(4, habit.getId());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Удаление привычки по id
     * @param habitId id привычки
     * @return true, если удаление прошло успешно, иначе false
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public boolean deleteById(Long habitId) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(DELETE_HABIT)) {
            statement.setLong(1, habitId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Преобразование строки результата запроса в объект HabitEntity.
     * @param resultSet Результат запроса
     * @return Объект HabitEntity, соответствующий строке результата
     * @throws SQLException В случае ошибок при работе с результатом запроса
     */
    @Override
    public HabitEntity mapRowToEntity(ResultSet resultSet) throws SQLException {
        return new HabitEntity(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("frequency"),
                resultSet.getDate("created_date").toLocalDate(),
                resultSet.getLong("user_id")
        );
    }
}
