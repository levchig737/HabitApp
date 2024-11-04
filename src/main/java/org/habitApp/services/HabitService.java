package org.habitApp.services;

import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.HabitAlreadyCompletedException;
import org.habitApp.models.Period;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для управления привычками (CRUD)
 * Позволяет создавать, редактировать, удалять и просматривать привычки пользователя.
 */
@Loggable
@Service
public class HabitService {
    @Autowired
    private final HabitRepository habitRepository;

    @Autowired
    private final HabitComletionHistoryRepository habitComletionHistoryRepository;

    public HabitService(HabitRepository habitRepository, HabitComletionHistoryRepository habitComletionHistoryRepository) {
        this.habitRepository = habitRepository;
        this.habitComletionHistoryRepository = habitComletionHistoryRepository;
    }

    public HabitEntity getHabitById(long habitId, UserEntity currentUser) throws SQLException, HabitNotFoundException, UnauthorizedAccessException {
        HabitEntity habit = habitRepository.getHabitById(habitId);
        if (habit == null) {
            throw new HabitNotFoundException("Привычка с ID " + habitId + " не найдена.");
        }

        // Проверка принадлежности привычки текущему пользователю
        if (habit.getUserId() != (currentUser.getId())) {
            throw new UnauthorizedAccessException("Привычка не принадлежит текущему пользователю.");
        }
        return habit;
    }

    /**
     * Создание новой привычки
     *
     * @param user        пользователь, создающий привычку
     * @param name        название привычки
     * @param description описание привычки
     * @param frequency   частота выполнения привычки
     */
    public void createHabit(UserEntity user, String name, String description, Period frequency) throws SQLException {
        HabitEntity habit = new HabitEntity(name, description, frequency.getPeriodName(), LocalDate.now(), user.getId());
        habitRepository.createHabit(habit);
        System.out.println("Привычка \"" + name + "\" создана для пользователя " + user.getEmail() + ".");
    }

    /**
     * Редактирование привычки
     *
     * @param habitId        привычка для редактирования
     * @param newName        новое название
     * @param newDescription новое описание
     * @param newFrequency   новая частота выполнения
     */
    public void updateHabit(long habitId, String newName, String newDescription, Period newFrequency) throws SQLException {
        HabitEntity habit = habitRepository.getHabitById(habitId);
        if (habit == null) {
            throw new HabitNotFoundException("Habit not found.");
        }
        habit.setName(newName);
        habit.setDescription(newDescription);
        habit.setFrequency(newFrequency.toString());

        habitRepository.updateHabit(habitId, habit);
        System.out.println("Привычка обновлена: " + habit.getName());
    }

    /**
     * Удаление привычки
     *
     * @param habitId привычка для удаления
     */
    public void deleteHabit(long habitId, UserEntity currentUser) throws SQLException {
        HabitEntity habit = habitRepository.getHabitById(habitId);
        if (habit == null) {
            throw new HabitNotFoundException("Habit not found.");
        }

        long userId = habit.getUserId();
        if (userId == currentUser.getId()) {
            habitRepository.deleteHabit(habitId);
            System.out.println("Привычка \"" + habit.getName() + "\" была удалена.");
        } else {
            throw new UnauthorizedAccessException("Habit does not belong to the current user.");
        }
    }

    /**
     * Получение всех привычек пользователя
     *
     * @param user текущий пользователь
     * @return список привычек пользователя
     */
    public List<HabitEntity> getAllHabits(UserEntity user) throws SQLException {
        return habitRepository.getHabitsByUser(user);
    }

    /**
     * Получение списка всех привычек для всех пользователей
     *
     * @param currentUser текущий пользователь
     * @return список всех привычек
     */
    public List<HabitEntity> getAllHabitsAdmin(UserEntity currentUser) throws SQLException {
        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        return habitRepository.getAllHabits();
    }

    /**
     * Отметить привычку как выполненную
     *
     * @param habitId привычка, которую нужно отметить сегодня
     */
    public void markHabitAsCompleted(long habitId) throws SQLException {
        List<LocalDate> completionHistory = habitComletionHistoryRepository.getCompletionHistoryForHabit(habitId);
        HabitEntity habit = habitRepository.getHabitById(habitId);

        if (completionHistory.isEmpty() || !Objects.equals(completionHistory.get(completionHistory.size() - 1), LocalDate.now())) {
            habitComletionHistoryRepository.addComletionDateByHabitIdUserIs(habit.getId(), habit.getUserId(), LocalDate.now());
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
    public HabitReportDto generateProgressReport(HabitEntity habit, Period period) throws SQLException {
        int streak = calculateCurrentStreak(habit);
        double completionPercentage = calculateCompletionPercentage(habit, period);
        int completionCount = calculateHabitCompletedByPeriod(habit, period);
        HabitReportDto habitReportDto = new HabitReportDto(habit.getId(), streak, completionPercentage, period, completionCount);
        return habitReportDto;
    }
}
