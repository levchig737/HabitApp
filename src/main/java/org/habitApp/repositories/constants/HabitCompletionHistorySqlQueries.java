// package org.habitApp.repositories.constants;
package org.habitApp.repositories.constants;

/**
 * Список констант запросов к таблице habit_completion_history
 */
public class HabitCompletionHistorySqlQueries {

    public static final String CREATE_COMPLETION_HISTORY =
            "INSERT INTO habit_completion_history (habit_id, user_id, completion_date) VALUES (?, ?, ?)";

    public static final String GET_COMPLETION_HISTORY_FOR_HABIT =
            "SELECT completion_date FROM habit_completion_history WHERE habit_id = ?";

    public static final String ADD_COMPLETION_DATE_BY_HABIT_ID_USER_ID =
            "INSERT INTO habit_completion_history (id, habit_id, user_id, completion_date) VALUES (?, ?, ?, ?)";

    public static final String GET_COMPLETION_HISTORY_BY_ID =
            "SELECT * FROM habit_completion_history WHERE id = ?";

    public static final String GET_ALL_COMPLETION_HISTORY =
            "SELECT * FROM habit_completion_history";

    public static final String UPDATE_COMPLETION_HISTORY =
            "UPDATE habit_completion_history SET habit_id = ?, user_id = ?, completion_date = ? WHERE id = ?";

    public static final String DELETE_COMPLETION_HISTORY_BY_ID =
            "DELETE FROM habit_completion_history WHERE id = ?";
}
