package org.habitApp.models;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;


/**
 * Модель Habit (Привычка)
 * Описывает привычку пользователя, включает в себя название, описание, частоту выполнения и историю выполнения.
 */
public class Habit {
    private int id;
    private String name;
    private String description;
    private String frequency; // ежедневная или еженедельная
    private LocalDate createdDate;
    private int userId;

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
        Random random = new Random();
        this.id = random.nextInt();
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

    public void setId(int id) {
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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
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

    public int getUserId() {
        return userId;
    }
}
