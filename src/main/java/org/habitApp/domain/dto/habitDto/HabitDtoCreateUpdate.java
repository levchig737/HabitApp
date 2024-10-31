package org.habitApp.domain.dto.habitDto;

public class HabitDtoCreateUpdate {
    private String name;
    private String description;
    private String frequency;

    public HabitDtoCreateUpdate(){}

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
