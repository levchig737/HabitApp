package org.habitApp.domain.entities;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Сущность для представления истории выполнения привычки.
 */
public class HabitCompletionHistoryEntity {
    private long id;
    private long habitId;
    private long userId;
    private LocalDate completionDate;

    /**
     * Конструктор HabitCompletionHistoryEntity
     * @param id id
     * @param habitId habitId
     * @param userId userId
     * @param completionDate completionDate
     */
    public HabitCompletionHistoryEntity(long id, long habitId, long userId, LocalDate completionDate) {
        this.id = id;
        this.habitId = habitId;
        this.userId = userId;
        this.completionDate = completionDate;
    }

    // Геттеры и сеттеры
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getHabitId() { return habitId; }
    public void setHabitId(long habitId) { this.habitId = habitId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
}
