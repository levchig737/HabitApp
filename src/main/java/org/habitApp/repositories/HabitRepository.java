package org.habitApp.repositories;

import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс репозитория для работы с сущностью HabitEntity.
 * Определяет дополнительные методы для специфичных операций с привычками.
 */
public interface HabitRepository extends BaseRepository<Long, HabitEntity> {

    /**
     * Получение списка всех привычек, связанных с конкретным пользователем.
     *
     * @param user пользователь, чьи привычки нужно получить
     * @return список привычек пользователя
     * @throws SQLException ошибка при работе с БД
     */
    List<HabitEntity> getHabitsByUser(UserEntity user) throws SQLException;
}
