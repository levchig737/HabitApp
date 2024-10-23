package habitApp.services;

import habitApp.models.Habit;
import habitApp.models.Period;
import habitApp.models.User;
import habitApp.repositories.HabitComletionHistoryRepository;
import habitApp.repositories.HabitRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для управления привычками (CRUD)
 * Позволяет создавать, редактировать, удалять и просматривать привычки пользователя.
 */
public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitComletionHistoryRepository habitComletionHistoryRepository;

    public HabitService(HabitRepository habitRepository, HabitComletionHistoryRepository habitComletionHistoryRepository) {
        this.habitRepository = habitRepository;
        this.habitComletionHistoryRepository = habitComletionHistoryRepository;
    }

    /**
     * Создание новой привычки
     * @param user пользователь, создающий привычку
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки
     * @throws SQLException ошибка работы с БД
     */
    public void createHabit(User user, String name, String description, Period frequency) throws SQLException {
        Habit habit = new Habit(name, description, frequency.getPeriodName(), LocalDate.now(), user.getId());
        habitRepository.createHabit(habit);
        System.out.println("Привычка \"" + name + "\" создана для пользователя " + user.getEmail() + ".");
    }

    /**
     * Редактирование привычки
     * @param habit привычка для редактирования
     * @param newName новое название
     * @param newDescription новое описание
     * @param newFrequency новая частота выполнения
     * @throws SQLException ошибка работы с БД
     */
    public void updateHabit(Habit habit, String newName, String newDescription, Period newFrequency) throws SQLException {
        habit.setName(newName);
        habit.setDescription(newDescription);
        habit.setFrequency(newFrequency.getPeriodName());
        habitRepository.updateHabit(habit);
        System.out.println("Привычка обновлена: " + habit.getName());
    }

    /**
     * Удаление привычки
     * @param habit привычка для удаления
     * @throws SQLException ошибка работы с БД
     */
    public void deleteHabit(Habit habit) throws SQLException {
        habitRepository.deleteHabit(habit);
        System.out.println("Привычка \"" + habit.getName() + "\" была удалена.");
    }

    /**
     * Получение всех привычек пользователя
     * @param user текущий пользователь
     * @return список привычек пользователя
     * @throws SQLException ошибка работы с БД
     */
    public List<Habit> getAllHabits(User user) throws SQLException {
        return habitRepository.getHabitsByUser(user);
    }

    /**
     * Получение списка всех привычек для всех пользователей
     * @param currentUser текущий пользователь
     * @return список всех привычек
     * @throws SQLException ошибка работы с БД
     * @throws IllegalAccessException доступ для админа
     */
    public List<Habit> getAllHabitsAdmin(User currentUser) throws SQLException, IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        return habitRepository.getAllHabits();
    }

    /**
     * Отметить привычку как выполненную
     * @param habit привычка, которую нужно отметить сегодня
     */
    public void markHabitAsCompleted(Habit habit) throws SQLException {
        List<LocalDate> completionHistory = habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId());

        if (completionHistory.isEmpty()
                || !Objects.equals(completionHistory.get(completionHistory.size() - 1),
                LocalDate.now())) {
            habitComletionHistoryRepository.addComletionDateByHabitIdUserIs(
                    habit.getId(), habit.getUserId(), LocalDate.now());
        }
        else {
            throw new IllegalArgumentException("Вы сегодня уже выполняли эту привычку");
        }
    }

    /**
     * Генерация статистики выполнения привычки за указанный период (день, неделя, месяц)
     * @param habit привычка
     * @param period период ("day", "week", "month")
     * @return статистика выполнения привычки
     */
    public int calculateHabitCompletedByPeriod(Habit habit, Period period) throws SQLException {
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
     * @param habit привычка
     * @return текущий streak
     */
    public int calculateCurrentStreak(Habit habit) throws SQLException {
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
     * @param habit привычка
     * @param period период ("day", "week", "month")
     * @return процент успешного выполнения привычки
     */
    public double calculateCompletionPercentage(Habit habit, Period period) throws SQLException {
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
     * @param habit привычка
     * @param period период ("day", "week", "month")
     * @return отчет о прогрессе
     */
    public String generateProgressReport(Habit habit, Period period) throws SQLException {
        int streak = calculateCurrentStreak(habit);
        double completionPercentage = calculateCompletionPercentage(habit, period);
        int completionCount = calculateHabitCompletedByPeriod(habit, period);
        return "Отчет по привычке \"" + habit.getName() + "\":\n" +
                "Текущий streak: " + streak + " дней подряд\n" +
                "Процент выполнения за период (" + period + "): " + String.format("%.2f", completionPercentage) + "%\n" +
                "Привычка за период (" + period + ") была выполнена " + completionCount + " раз(а).\n" +
                "Описание: " + habit.getDescription();
    }
}
