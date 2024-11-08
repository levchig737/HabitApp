package org.habitApp.domain.entities;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;


/**
 * Модель Habit (Привычка)
 * Описывает привычку пользователя, включает в себя название, описание, частоту выполнения и историю выполнения.
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HabitEntity {
    private long id;
    private String name;
    private String description;
    private String frequency; // ежедневная или еженедельная
    private LocalDate createdDate;
    private long userId;

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
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки (ежедневно, еженедельно)
     * @param createdDate дата создания привычки
     * @param userId пользователь id
     */
    public HabitEntity(String name, String description, String frequency, LocalDate createdDate, long userId) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdDate = createdDate;
        this.userId = userId;
    }
}
