package org.habitApp.services.impl;

import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.HabitAlreadyCompletedException;
import org.habitApp.models.Period;
import org.habitApp.repositories.HabitCompletionHistoryRepository;
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
public class HabitServiceImpl implements HabitService {
    private final HabitRepositoryImpl habitRepository;
    private final HabitCompletionHistoryRepository habitComletionHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(HabitServiceImpl.class);

    /**
     * Конструктор HabitService
     * @param habitRepository habitRepository
     * @param habitComletionHistoryRepository habitComletionHistoryRepository
     */
    public HabitServiceImpl(HabitRepositoryImpl habitRepository, HabitCompletionHistoryRepository habitComletionHistoryRepository) {
        this.habitRepository = habitRepository;
        this.habitComletionHistoryRepository = habitComletionHistoryRepository;
    }

    /**
     * Получение привычки по id
     * @param habitId id
     * @return HabitEnity
     */
    @Override
    public HabitEntity getHabitById(long habitId) throws SQLException, HabitNotFoundException,
            UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

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
     */
    @Override
    public void createHabit(String name, String description, Period frequency)
            throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        HabitEntity habit = new HabitEntity(name, description, frequency.getPeriodName(), LocalDate.now(), currentUser.getId());
        habitRepository.create(habit);
        logger.info("Привычка \" {} \" создана для пользователя: {}.", name, currentUser.getEmail());
    }

    /**
     * Редактирование привычки
     *
     * @param habitId        привычка для редактирования
     * @param newName        новое название
     * @param newDescription новое описание
     * @param newFrequency   новая частота выполнения
     */
    @Override
    public void updateHabit(long habitId, String newName, String newDescription, Period newFrequency)
            throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        Optional<HabitEntity> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            habit.get().setId(habitId);
            habit.get().setName(newName);
            habit.get().setDescription(newDescription);
            habit.get().setFrequency(newFrequency.toString());

            habitRepository.update(habit.get());
            logger.info("Привычка обновлена: {}", habit.get().getName());
        } else {
            throw new HabitNotFoundException("Habit not found.");
        }
    }

    /**
     * Удаление привычки
     *
     * @param habitId привычка для удаления
     */
    @Override
    public void deleteHabit(long habitId) throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        Optional<HabitEntity> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            long userId = habit.get().getUserId();
            if (userId == currentUser.getId()) {
                habitRepository.deleteById(habitId);
                logger.info("Привычка \"{} \" была удалена.",
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
     * @return список привычек пользователя
     */
    @Override
    public List<HabitEntity> getAllHabits() throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        return habitRepository.getHabitsByUser(currentUser);
    }

    /**
     * Получение списка всех привычек для всех пользователей
     *
     * @return список всех привычек
     */
    @Override
    public List<HabitEntity> getAllHabitsAdmin() throws SQLException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        return habitRepository.findAll();
    }

    /**
     * Отметить привычку как выполненную
     *
     * @param habitId привычка, которую нужно отметить сегодня
     */
    @Override
    public void markHabitAsCompleted(long habitId) throws SQLException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

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
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

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
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

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
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

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
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        int streak = calculateCurrentStreak(habit);
        double completionPercentage = calculateCompletionPercentage(habit, period);
        int completionCount = calculateHabitCompletedByPeriod(habit, period);
        HabitReportDto habitReportDto = new HabitReportDto(habit.getId(), streak, completionPercentage, period, completionCount);
        return habitReportDto;
    }
}
