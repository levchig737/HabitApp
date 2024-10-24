package org.habitApp.dto;

import org.habitApp.dto.enums.HealthStatus;

public record HealthResponseDto(HealthStatus healthStatus) {
}
