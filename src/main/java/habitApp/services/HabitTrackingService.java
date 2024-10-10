package habitApp.services;

import habitApp.models.Habit;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
     * Получить количество выполнений привычки
     * @param habit привычка, по которой нужно получить статистику
     * @return количество выполнений привычки
     */
    public int getCompletionCount(Habit habit) {
        return habit.getCompletionHistory().size();
    }
}
