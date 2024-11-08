package org.habitApp.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.habitApp.domain.entities.HabitCompletionHistoryEntity;
import org.habitApp.repositories.HabitCompletionHistoryRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.habitApp.repositories.constants.HabitCompletionHistorySqlQueries.*;

/**
 * Реализация репозитория для работы с историей выполнения привычек.
 */
@Repository
@RequiredArgsConstructor
public class HabitCompletionHistoryRepositoryImpl implements HabitCompletionHistoryRepository {

    private final DataSource dataSource;

    /**
     * Получение истории выполнения для конкретной привычки
     *
     * @param habitId ID привычки
     * @return список дат выполнения привычки
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public List<LocalDate> getCompletionHistoryForHabit(long habitId) throws SQLException {
        List<LocalDate> completionHistory = new ArrayList<>();
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_COMPLETION_HISTORY_FOR_HABIT)) {
            statement.setLong(1, habitId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    completionHistory.add(resultSet.getDate("completion_date").toLocalDate());
                }
            }
        }
        return completionHistory;
    }

    /**
     * Добавление даты выполнения привычки для пользователя
     *
     * @param habitId ID привычки
     * @param userId ID пользователя
     * @param completionDate дата выполнения
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public void addCompletionDateByHabitIdUserId(long habitId, long userId, LocalDate completionDate) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(ADD_COMPLETION_DATE_BY_HABIT_ID_USER_ID)) {
            statement.setLong(1, new Random().nextLong());
            statement.setLong(2, habitId);
            statement.setLong(3, userId);
            statement.setDate(4, Date.valueOf(completionDate));
            statement.executeUpdate();
        }
    }

    /**
     * Создание новой записи истории выполнения привычки
     * @param historyEntity сущность истории выполнения
     * @return созданная сущность
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public HabitCompletionHistoryEntity create(HabitCompletionHistoryEntity historyEntity) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(CREATE_COMPLETION_HISTORY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, historyEntity.getHabitId());
            statement.setLong(2, historyEntity.getUserId());
            statement.setDate(3, Date.valueOf(historyEntity.getCompletionDate()));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    historyEntity.setId(generatedKeys.getLong(1));
                }
            }
        }
        return historyEntity;
    }

    /**
     * Поиск записи по ID
     *
     * @param id ID записи
     * @return Optional с записью, если найдена
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public Optional<HabitCompletionHistoryEntity> findById(Long id) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_COMPLETION_HISTORY_BY_ID)) {
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
     * Получение всех записей истории выполнения
     *
     * @return список всех записей истории выполнения
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public List<HabitCompletionHistoryEntity> findAll() throws SQLException {
        List<HabitCompletionHistoryEntity> historyList = new ArrayList<>();
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_ALL_COMPLETION_HISTORY)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    historyList.add(mapRowToEntity(resultSet));
                }
            }
        }
        return historyList;
    }

    /**
     * Обновление записи истории выполнения
     *
     * @param historyEntity обновленная сущность
     * @return true, если обновление прошло успешно
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public boolean update(HabitCompletionHistoryEntity historyEntity) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(UPDATE_COMPLETION_HISTORY)) {
            statement.setLong(1, historyEntity.getHabitId());
            statement.setLong(2, historyEntity.getUserId());
            statement.setDate(3, Date.valueOf(historyEntity.getCompletionDate()));
            statement.setLong(4, historyEntity.getId());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Удаление записи по ID
     *
     * @param id ID записи
     * @return true, если удаление прошло успешно
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public boolean deleteById(Long id) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(DELETE_COMPLETION_HISTORY_BY_ID)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Преобразование строки результата запроса в объект HabitCompletionHistoryEntity
     *
     * @param resultSet результат запроса
     * @return сущность HabitCompletionHistoryEntity
     * @throws SQLException ошибка работы с БД
     */
    @Override
    public HabitCompletionHistoryEntity mapRowToEntity(ResultSet resultSet) throws SQLException {
        return new HabitCompletionHistoryEntity(
                resultSet.getLong("id"),
                resultSet.getLong("habit_id"),
                resultSet.getLong("user_id"),
                resultSet.getDate("completion_date").toLocalDate()
        );
    }
}
