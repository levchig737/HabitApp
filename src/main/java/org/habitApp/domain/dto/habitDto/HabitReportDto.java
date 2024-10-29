package org.habitApp.domain.dto.habitDto;

import org.habitApp.models.Period;

import java.util.UUID;

public class HabitReportDto {
    private UUID id;
    private int streak;
    private double percentage;
    private Period period;
    private int completionCount;

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public HabitReportDto(UUID id, int streak, double percentage, Period period, int completionCount) {
        this.id = id;
        this.streak = streak;
        this.percentage = percentage;
        this.period = period;
        this.completionCount = completionCount;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public UUID getId() {
        return id;
    }

    public int getStreak() {
        return streak;
    }

    public double getPercentage() {
        return percentage;
    }

    public Period getPeriod() {
        return period;
    }
}
