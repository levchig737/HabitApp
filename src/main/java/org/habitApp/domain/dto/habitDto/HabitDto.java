package org.habitApp.domain.dto.habitDto;

import java.util.UUID;

public class HabitDto {
    private UUID id;
    private String name;
    private String description;
    private String frequency;
    private UUID userId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "HabitDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency='" + frequency + '\'' +
                ", userId=" + userId +
                '}';
    }
}
