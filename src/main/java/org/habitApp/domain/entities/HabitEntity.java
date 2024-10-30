package org.habitApp.domain.entities;


import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;


/**
 * Модель Habit (Привычка)
 * Описывает привычку пользователя, включает в себя название, описание, частоту выполнения и историю выполнения.
 */
@Getter
@Setter
public class HabitEntity {
    private UUID id;
    private String name;
    private String description;
    private String frequency; // ежедневная или еженедельная
    private LocalDate createdDate;
    private UUID userId;

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency='" + frequency + '\'' +
                ", createdDate=" + createdDate +
                ", userId=" + userId +
                '}';
    }

    /**
     * Конструктор Habit
     * @param id индекс привычки
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки (ежедневно, еженедельно)
     * @param createdDate дата создания привычки
     * @param userId пользователь id
     */
    public HabitEntity(UUID id, String name, String description, String frequency, LocalDate createdDate, UUID userId) {
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
    public static HabitEntity CreateHabit(String name, String description, String frequency, LocalDate createdDate, UUID userId) {
        return new HabitEntity(UUID.randomUUID(), name, description, frequency, createdDate, userId);
    }

    /**
     * Преобразование строки из ResultSet в объект Habit
     * @param resultSet строка результата
     * @return объект Habit
     * @throws SQLException ошибка работы с БД
     */
    public static HabitEntity mapRowToHabit(ResultSet resultSet) throws SQLException {
        HabitEntity habit = new HabitEntity(
                (UUID) resultSet.getObject("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("frequency"),
                resultSet.getDate("created_date").toLocalDate(),
                (UUID) resultSet.getObject("user_id")
        );
        return habit;
    }
}
