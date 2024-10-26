package org.habitApp.models;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;


/**
 * Модель Habit (Привычка)
 * Описывает привычку пользователя, включает в себя название, описание, частоту выполнения и историю выполнения.
 */
public class Habit {
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
    public Habit(UUID id, String name, String description, String frequency, LocalDate createdDate, UUID userId) {
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
    public static Habit CreateHabit(String name, String description, String frequency, LocalDate createdDate, UUID userId) {
        return new Habit(UUID.randomUUID(), name, description, frequency, createdDate, userId);
    }

    /**
     * Преобразование строки из ResultSet в объект Habit
     * @param resultSet строка результата
     * @return объект Habit
     * @throws SQLException ошибка работы с БД
     */
    public static Habit mapRowToHabit(ResultSet resultSet) throws SQLException {
        Habit habit = new Habit(
                (UUID) resultSet.getObject("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("frequency"),
                resultSet.getDate("created_date").toLocalDate(),
                (UUID) resultSet.getObject("user_id")
        );
        return habit;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFrequency() {
        return frequency;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public UUID getUserId() {
        return userId;
    }
}
