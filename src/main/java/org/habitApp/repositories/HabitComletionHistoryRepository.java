package org.habitApp.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.habitApp.repositories.constants.HabitCompletionHistorySqlQueries.*;

@Repository
public class HabitComletionHistoryRepository {

    private final DataSource dataSource;

    /**
     * Конструктор репозитория
     *
     */
    public HabitComletionHistoryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Получение истории выполнения для конкретной привычки
     *
     * @param habitId ID привычки
     * @return список дат выполнения привычки
     * @throws SQLException ошибка работы с БД
     */
    public List<LocalDate> getCompletionHistoryForHabit(long habitId) throws SQLException {
        List<LocalDate> completionHistory = new ArrayList<>();

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_COMPLETION_HISTORY_FOR_HABIT)) {
            statement.setLong(1, habitId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDate completionDate = resultSet.getDate("completion_date").toLocalDate();
                    completionHistory.add(completionDate);
                }
            }
        }
        return completionHistory;
    }

    /**
     * Добавление habitComletion
     * @param habitId habit id
     * @param userId user id
     * @throws SQLException ошибка работы с БД
     */
    public void addComletionDateByHabitIdUserIs(long habitId, long userId, LocalDate completionDate) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(ADD_COMPLETION_DATE_BY_HABIT_ID_USER_ID)) {
            Random random = new Random();
            statement.setInt(1, random.nextInt());
            statement.setLong(2, habitId);
            statement.setLong(3, userId);
            statement.setDate(4, Date.valueOf(completionDate));
            statement.executeUpdate();
        }
    }
}
