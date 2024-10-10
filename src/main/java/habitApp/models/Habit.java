package habitApp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель Habit (Привычка)
 * Описывает привычку пользователя, включает в себя название, описание, частоту выполнения и историю выполнения.
 */
@Getter @Setter @ToString
public class Habit {
    private String name;
    private String description;
    private String frequency; // ежедневная или еженедельная
    private LocalDate createdDate;
    private List<LocalDate> completionHistory = new ArrayList<>();

    /**
     * Конструктор Habit
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки (ежедневно, еженедельно)
     */
    public Habit(String name, String description, String frequency) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdDate = LocalDate.now();
    }

    /**
     * Получение последнего элмента completionHistory
     * @return LocalDate
     */
    public LocalDate getLastCompletionHistory(){
        if (this.completionHistory.isEmpty()) {
            return null;
        }
        else {
            return this.completionHistory.get(this.completionHistory.size()-1);
        }
    }

    /**
     * Добавление даты выполнения в историю привычки
     * @param date дата выполнения привычки
     */
    public void addCompletion(LocalDate date) {
        this.completionHistory.add(date);
    }
}

