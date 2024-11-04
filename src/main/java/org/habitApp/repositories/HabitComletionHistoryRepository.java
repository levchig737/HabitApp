package org.habitApp.repositories;

import org.habitApp.annotations.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Loggable
@Repository
public class HabitComletionHistoryRepository {
    @Autowired
    private DataSource dataSource;

    /**
     * Конструктор репозитория
     *
     */
    @Autowired
    public HabitComletionHistoryRepository() {
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
        String sql = "SELECT completion_date FROM habit_completion_history WHERE habit_id = ?";

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
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
        String sql = "INSERT INTO habit_completion_history (id, habit_id, user_id, completion_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            Random random = new Random();
            statement.setInt(1, random.nextInt());
            statement.setLong(2, habitId);
            statement.setLong(3, userId);
            statement.setDate(4, Date.valueOf(completionDate));
            statement.executeUpdate();
        }
    }
}
