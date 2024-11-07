package org.habitApp.repositories;

import org.habitApp.domain.entities.HabitCompletionHistoryEntity;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для работы с историей выполнения привычек.
 */
public interface HabitCompletionHistoryRepository extends BaseRepository<Long, HabitCompletionHistoryEntity> {

    /**
     * Получение истории выполнения для конкретной привычки.
     *
     * @param habitId ID привычки
     * @return список дат выполнения привычки
     * @throws SQLException ошибка работы с БД
     */
    List<LocalDate> getCompletionHistoryForHabit(long habitId) throws SQLException;

    /**
     * Добавление даты выполнения привычки для пользователя.
     *
     * @param habitId ID привычки
     * @param userId ID пользователя
     * @param completionDate дата выполнения
     * @throws SQLException ошибка работы с БД
     */
    void addCompletionDateByHabitIdUserId(long habitId, long userId, LocalDate completionDate) throws SQLException;
}
