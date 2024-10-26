package org.habitApp.dto;

public record HealthResponseDto(HealthStatus healthStatus) {
    public enum HealthStatus {
        UP, DOWN
    }
}
