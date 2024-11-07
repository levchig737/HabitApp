package org.habitApp.repositories.constants;


/**
 * Список констант запросов к таблице habit_completion_history
 */
public class HabitCompletionHistorySqlQueries {
    public static final String GET_COMPLETION_HISTORY_FOR_HABIT = "SELECT completion_date FROM habit_completion_history WHERE habit_id = ?";
    public static final String ADD_COMPLETION_DATE_BY_HABIT_ID_USER_ID = "INSERT INTO habit_completion_history (id, habit_id, user_id, completion_date) VALUES (?, ?, ?, ?)";
}

