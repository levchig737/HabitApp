package org.habitApp.services;

import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.HealthResponseDto;

@Loggable
public class HealthCheckService {
    public HealthResponseDto.HealthStatus getHealthStatus() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return HealthResponseDto.HealthStatus.UP;
    }
}
