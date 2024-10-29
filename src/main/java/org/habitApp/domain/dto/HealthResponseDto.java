package org.habitApp.domain.dto;

public record HealthResponseDto(HealthStatus healthStatus) {
    public enum HealthStatus {
        UP, DOWN
    }
}
