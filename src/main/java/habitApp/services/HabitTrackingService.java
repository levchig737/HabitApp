package habitApp.services;

import habitApp.models.Habit;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для отслеживания выполнения привычек и генерации статистики
 */
public class HabitTrackingService {

    /**
     * Отметить привычку как выполненную
     * @param habit привычка, которую нужно отметить сегодня
     */
    public void markHabitAsCompleted(Habit habit) {
        LocalDate localDate = habit.getLastCompletionHistory();
        if (localDate == null) {
            habit.addCompletion(LocalDate.now());
            return;
        }

        if (!localDate.equals(LocalDate.now())) {
            habit.addCompletion(LocalDate.now());
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
    public int generateHabitStatistics(Habit habit, String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period.toLowerCase()) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Неверный период. Используйте 'day', 'week' или 'month'.");
        };

        // Подсчитываем количество выполнений за указанный период
        List<LocalDate> completionsInPeriod = habit.getCompletionHistory().stream()
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(now))
                .toList();

        return completionsInPeriod.size();
    }
}
