package org.habitApp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель Habit (Привычка)
 * Описывает привычку пользователя, включает в себя название, описание, частоту выполнения и историю выполнения.
 */
@Getter
@Setter
@ToString
public class Habit {
    private int id;
    private String name;
    private String description;
    private String frequency; // ежедневная или еженедельная
    private LocalDate createdDate;
    private int userId;

    /**
     * Конструктор Habit
     * @param id индекс привычки
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки (ежедневно, еженедельно)
     * @param createdDate дата создания привычки
     * @param userId пользователь id
     */
    public Habit(int id, String name, String description, String frequency, LocalDate createdDate, int userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdDate = createdDate;
        this.userId = userId;
    }

    /**
     * Конструктор Habit
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки (ежедневно, еженедельно)
     * @param createdDate дата создания привычки
     * @param userId пользователь id
     */
    public Habit(String name, String description, String frequency, LocalDate createdDate, int userId) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdDate = createdDate;
        this.userId = userId;
    }

    /**
     * Преобразование строки из ResultSet в объект Habit
     * @param resultSet строка результата
     * @return объект Habit
     * @throws SQLException ошибка работы с БД
     */
    public static Habit mapRowToHabit(ResultSet resultSet) throws SQLException {
        Habit habit = new Habit(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("frequency"),
                resultSet.getDate("created_date").toLocalDate(),
                resultSet.getInt("user_id")
        );
        return habit;
    }
}
