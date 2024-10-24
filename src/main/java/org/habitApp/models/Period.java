package org.habitApp.models;

/**
 * Enum для периода
 */
public enum Period {
    DAY("day"),    // Ежедневный период
    WEEK("week"),  // Еженедельный период
    MONTH("month"); // Ежемесячный период

    private final String periodName;

    /**
     * Конструктор
     * @param periodName имя периода
     */
    Period(String periodName) {
        this.periodName = periodName;
    }

    /**
     * Геттер
     * @return string имя периода
     */
    public String getPeriodName() {
        return periodName;
    }

    /**
     * Метод для приведения строки к enum
     * @param periodName string период
     * @return enum период
     */
    public static Period fromString(String periodName) {
        for (Period period : Period.values()) {
            if (period.periodName.equalsIgnoreCase(periodName)) {
                return period;
            }
        }
        throw new IllegalArgumentException("Unknown period: " + periodName);
    }
}
