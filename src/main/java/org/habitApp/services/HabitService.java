package org.habitApp.services;

import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.models.Period;

import java.sql.SQLException;
import java.util.List;

public interface HabitService {

    /**
     * Получение привычки по id
     * @param habitId id
     * @param currentUser текущий пользователь
     * @return HabitEntity
     * @throws SQLException, HabitNotFoundException, UnauthorizedAccessException
     */
    HabitEntity getHabitById(long habitId, UserEntity currentUser) throws SQLException, HabitNotFoundException, UnauthorizedAccessException;

    /**
     * Создание новой привычки
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки
     * @param currentUser текущий пользователь
     * @throws SQLException, UnauthorizedAccessException
     */
    void createHabit(String name, String description, Period frequency, UserEntity currentUser) throws SQLException, UnauthorizedAccessException;

    /**
     * Редактирование привычки
     * @param habitId id привычки
     * @param newName новое название
     * @param newDescription новое описание
     * @param newFrequency новая частота выполнения
     * @param currentUser текущий пользователь
     * @throws SQLException, UnauthorizedAccessException
     */
    void updateHabit(long habitId, String newName, String newDescription, Period newFrequency, UserEntity currentUser) throws SQLException, UnauthorizedAccessException;

    /**
     * Удаление привычки
     * @param habitId id привычки
     * @param currentUser текущий пользователь
     * @throws SQLException, UnauthorizedAccessException
     */
    void deleteHabit(long habitId, UserEntity currentUser) throws SQLException, UnauthorizedAccessException;

    /**
     * Получение всех привычек текущего пользователя
     * @param currentUser текущий пользователь
     * @return список привычек пользователя
     * @throws SQLException, UnauthorizedAccessException
     */
    List<HabitEntity> getAllHabits(UserEntity currentUser) throws SQLException, UnauthorizedAccessException;

    /**
     * Получение списка всех привычек для всех пользователей (только для админа)
     * @return список всех привычек
     * @throws SQLException, UnauthorizedAccessException
     */
    List<HabitEntity> getAllHabitsAdmin() throws SQLException, UnauthorizedAccessException;

    /**
     * Отметить привычку как выполненную
     * @param habitId id привычки
     * @throws SQLException, UnauthorizedAccessException
     */
    void markHabitAsCompleted(long habitId) throws SQLException, UnauthorizedAccessException;

    /**
     * Генерация статистики выполнения привычки за указанный период
     * @param habit привычка
     * @param period период ("day", "week", "month")
     * @return статистика выполнения привычки
     * @throws SQLException
     */
    int calculateHabitCompletedByPeriod(HabitEntity habit, Period period) throws SQLException;

    /**
     * Подсчет текущего streak выполнения привычки
     * @param habit привычка
     * @return текущий streak
     * @throws SQLException
     */
    int calculateCurrentStreak(HabitEntity habit) throws SQLException;

    /**
     * Процент успешного выполнения привычки за определенный период
     * @param habit привычка
     * @param period период ("day", "week", "month")
     * @return процент успешного выполнения привычки
     * @throws SQLException
     */
    double calculateCompletionPercentage(HabitEntity habit, Period period) throws SQLException;

    /**
     * Формирование отчета по прогрессу выполнения привычек
     * @param habit привычка
     * @param period период ("day", "week", "month")
     * @return отчет о прогрессе
     * @throws SQLException
     */
    HabitReportDto generateProgressReport(HabitEntity habit, Period period) throws SQLException;
}
