package habitApp.services;

import habitApp.models.Habit;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    public int calculateHabitCompletedByPeriod(Habit habit, String period) {
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

    /**
     * Подсчет текущего streak выполнения привычки
     * @param habit привычка
     * @return текущий streak
     */
    public int calculateCurrentStreak(Habit habit) {
        List<LocalDate> completions = habit.getCompletionHistory().stream()
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
    public double calculateCompletionPercentage(Habit habit, String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period.toLowerCase()) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Неверный период. Используйте 'day', 'week' или 'month'.");
        };

        long totalDays = ChronoUnit.DAYS.between(startDate, now);

        // Подсчитываем количество выполнений за указанный период
        long completionsInPeriod = habit.getCompletionHistory().stream()
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
    public String generateProgressReport(Habit habit, String period) {
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
