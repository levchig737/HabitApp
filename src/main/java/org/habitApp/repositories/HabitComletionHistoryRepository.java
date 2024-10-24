package org.habitApp.repositories;

import org.habitApp.database.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitComletionHistoryRepository {
    /**
     * Конструктор репозитория
     *
     */
    public HabitComletionHistoryRepository() {
    }

    /**
     * Получение истории выполнения для конкретной привычки
     *
     * @param habitId ID привычки
     * @return список дат выполнения привычки
     * @throws SQLException ошибка работы с БД
     */
    public List<LocalDate> getCompletionHistoryForHabit(int habitId) throws SQLException {
        List<LocalDate> completionHistory = new ArrayList<>();
        String sql = "SELECT completion_date FROM habit_completion_history WHERE habit_id = ?";

        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setInt(1, habitId);
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
    public void addComletionDateByHabitIdUserIs(int habitId, int userId, LocalDate completionDate) throws SQLException {
        String sql = "INSERT INTO habit_completion_history (habit_id, user_id, completion_date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = Database.connectToDatabase().prepareStatement(sql)) {
            statement.setInt(1, habitId);
            statement.setInt(2, userId);
            statement.setDate(3, Date.valueOf(completionDate));
            statement.executeUpdate();
        }
    }
}
