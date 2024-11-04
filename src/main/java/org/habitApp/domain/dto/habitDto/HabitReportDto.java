package org.habitApp.domain.dto.habitDto;

import org.habitApp.models.Period;

import java.util.UUID;

public class HabitReportDto {
    private long id;
    private int streak;
    private double completionPercentage;
    private int completionCount;
    private Period period;

    public HabitReportDto(){}

    public HabitReportDto(long id, int streak, double completionPercentage, Period period, int completionCount) {
        this.id = id;
        this.streak = streak;
        this.completionPercentage = completionPercentage;
        this.completionCount = completionCount;
        this.period = period;
    }

    // Геттеры и сеттеры
    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }
}
