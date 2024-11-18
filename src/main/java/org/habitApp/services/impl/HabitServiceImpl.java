package org.habitApp.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.HabitAlreadyCompletedException;
import org.habitApp.models.Period;
import org.habitApp.repositories.HabitCompletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.habitApp.repositories.impl.HabitRepositoryImpl;
import org.habitApp.services.HabitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для управления привычками (CRUD)
 * Позволяет создавать, редактировать, удалять и просматривать привычки пользователя.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final HabitCompletionHistoryRepository habitComletionHistoryRepository;

    /**
     * Получение привычки по id
     * @param habitId id
     * @param currentUser текущий пользователь
     * @return HabitEnity
     */
    @Override
    public HabitEntity getHabitById(long habitId, UserEntity currentUser) throws SQLException, HabitNotFoundException,
            UnauthorizedAccessException {
        Optional<HabitEntity> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {// Проверка принадлежности привычки текущему пользователю
            if (habit.get().getUserId() != (currentUser.getId())) {
                throw new UnauthorizedAccessException("Привычка не принадлежит текущему пользователю.");
            }
            return habit.orElse(null);
        } else {
            throw new HabitNotFoundException("Привычка с ID " + habitId + " не найдена.");
        }

    }

    /**
     * Создание новой привычки
     *
     * @param name        название привычки
     * @param description описание привычки
     * @param frequency   частота выполнения привычки
     * @param currentUser текущий пользователь
     */
    @Override
    public void createHabit(String name, String description, Period frequency, UserEntity currentUser)
            throws SQLException, UnauthorizedAccessException {
        HabitEntity habit = new HabitEntity(name, description, frequency.getPeriodName(), LocalDate.now(), currentUser.getId());
        habitRepository.create(habit);
        log.info("Привычка \" {} \" создана для пользователя: {}.", name, currentUser.getEmail());
    }

    /**
     * Редактирование привычки
     *
     * @param habitId        привычка для редактирования
     * @param newName        новое название
     * @param newDescription новое описание
     * @param newFrequency   новая частота выполнения
     * @param currentUser текущий пользователь
     */
    @Override
    public void updateHabit(long habitId, String newName, String newDescription, Period newFrequency, UserEntity currentUser)
            throws SQLException, UnauthorizedAccessException {
        Optional<HabitEntity> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            if (habit.get().getUserId() != (currentUser.getId())) {
                throw new UnauthorizedAccessException("Привычка не принадлежит текущему пользователю.");
            }

            habit.get().setId(habitId);
            habit.get().setName(newName);
            habit.get().setDescription(newDescription);
            habit.get().setFrequency(newFrequency.toString());

            habitRepository.update(habit.get());
            log.info("Привычка обновлена: {}", habit.get().getName());
        } else {
            throw new HabitNotFoundException("Habit not found.");
        }
    }

    /**
     * Удаление привычки
     *
     * @param habitId привычка для удаления
     * @param currentUser текущий пользователь
     */
    @Override
    public void deleteHabit(long habitId, UserEntity currentUser) throws SQLException, UnauthorizedAccessException {
        Optional<HabitEntity> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            long userId = habit.get().getUserId();
            if (userId == currentUser.getId()) {
                habitRepository.deleteById(habitId);
                log.info("Привычка \"{} \" была удалена.",
                        habit.get().getName());
            } else {
                throw new UnauthorizedAccessException("Habit does not belong to the current user.");
            }
        } else {
            throw new HabitNotFoundException("Habit not found.");
        }

    }

    /**
     * Получение всех привычек текущего пользователя
     *
     * @param currentUser текущий пользователь
     * @return список привычек пользователя
     */
    @Override
    public List<HabitEntity> getAllHabits(UserEntity currentUser) throws SQLException, UnauthorizedAccessException {

        return habitRepository.getHabitsByUser(currentUser);
    }

    /**
     * Получение списка всех привычек для всех пользователей
     *
     * @return список всех привычек
     */
    @Override
    public List<HabitEntity> getAllHabitsAdmin() throws SQLException {

        return habitRepository.findAll();
    }

    /**
     * Отметить привычку как выполненную
     *
     * @param habitId привычка, которую нужно отметить сегодня
     */
    @Override
    public void markHabitAsCompleted(long habitId) throws SQLException {

        List<LocalDate> completionHistory = habitComletionHistoryRepository.getCompletionHistoryForHabit(habitId);
        Optional<HabitEntity> habit = habitRepository.findById(habitId);

        if (completionHistory.isEmpty() || !Objects.equals(completionHistory.get(completionHistory.size() - 1), LocalDate.now())) {
            habitComletionHistoryRepository.addCompletionDateByHabitIdUserId(habit.get().getId(), habit.get().getUserId(), LocalDate.now());
        } else {
            throw new HabitAlreadyCompletedException("Вы сегодня уже выполняли эту привычку.");
        }
    }

    /**
     * Генерация статистики выполнения привычки за указанный период (день, неделя, месяц)
     *
     * @param habit  привычка
     * @param period период ("day", "week", "month")
     * @return статистика выполнения привычки
     */
    @Override
    public int calculateHabitCompletedByPeriod(HabitEntity habit, Period period) throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period.getPeriodName().toLowerCase()) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Неверный период. Используйте 'day', 'week' или 'month'.");
        };

        // Подсчитываем количество выполнений за указанный период
        List<LocalDate> completionsInPeriod = habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId())
                .stream()
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(now))
                .toList();

        return completionsInPeriod.size();
    }

    /**
     * Подсчет текущего streak выполнения привычки
     *
     * @param habit привычка
     * @return текущий streak
     */
    @Override
    public int calculateCurrentStreak(HabitEntity habit) throws SQLException {
        List<LocalDate> completions = habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId())
                .stream()
                .sorted()
                .toList();

        if (completions.isEmpty()) return 0;

        int streak = 1;
        LocalDate today = LocalDate.now();

        for (int i = completions.size() - 1; i > 0; i--) {
            LocalDate currentDate = completions.get(i);
            LocalDate previousDate = completions.get(i - 1);

            long daysBetween = ChronoUnit.DAYS.between(previousDate, currentDate);

            if (daysBetween == 1) {
                streak++;
            } else {
                break;
            }
        }

        // Проверяем, выполнена ли привычка сегодня или вчера для учета streak
        if (ChronoUnit.DAYS.between(completions.get(completions.size() - 1), today) > 1) {
            return 0; // Если больше одного дня пропущено, streak сбрасывается.
        }

        return streak;
    }

    /**
     * Процент успешного выполнения привычки за определенный период (день, неделя, месяц)
     *
     * @param habit  привычка
     * @param period период ("day", "week", "month")
     * @return процент успешного выполнения привычки
     */
    @Override
    public double calculateCompletionPercentage(HabitEntity habit, Period period) throws SQLException {

        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period.getPeriodName().toLowerCase()) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Неверный период. Используйте 'day', 'week' или 'month'.");
        };

        long totalDays = ChronoUnit.DAYS.between(startDate, now);

        // Подсчитываем количество выполнений за указанный период
        long completionsInPeriod = habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId())
                .stream()
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(now))
                .count();

        return (double) completionsInPeriod / totalDays * 100;
    }

    /**
     * Формирование отчета по прогрессу выполнения привычек
     *
     * @param habit  привычка
     * @param period период ("day", "week", "month")
     * @return отчет о прогрессе
     */
    @Override
    public HabitReportDto generateProgressReport(HabitEntity habit, Period period) throws SQLException {

        int streak = calculateCurrentStreak(habit);
        double completionPercentage = calculateCompletionPercentage(habit, period);
        int completionCount = calculateHabitCompletedByPeriod(habit, period);
        HabitReportDto habitReportDto = new HabitReportDto(habit.getId(), streak, completionPercentage, completionCount, period);
        return habitReportDto;
    }
}
